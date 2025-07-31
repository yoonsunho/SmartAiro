package com.example.smartAir.service;

import com.example.smartAir.domain.Device;
import com.example.smartAir.domain.MapData;
import com.example.smartAir.dto.MapDataRequest;
import com.example.smartAir.dto.MapMetaInfo;
import com.example.smartAir.dto.MapViewResponse;
import com.example.smartAir.repository.DeviceRepository;
import com.example.smartAir.repository.MapDataRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Service // 의존성 주입의 대상
public class MapDataService {

    private final MapDataRepository mapDataRepository;
    private final DeviceRepository deviceRepository;
    private final AwsS3Service awsS3Service; // S3 업로드 및 presigned URL 서비스

    private final S3Client s3Client;    // S3에서 파일 직접 다운로드용

    public MapDataService(MapDataRepository mapDataRepository,
                          DeviceRepository deviceRepository,
                          AwsS3Service awsS3Service,
                          S3Client s3Client) {
        this.mapDataRepository = mapDataRepository;
        this.deviceRepository = deviceRepository;
        this.awsS3Service = awsS3Service;
        this.s3Client = s3Client;
    }
    /*
     임베디드에서 받은 yamlFile, pgmFile을 S3에 업로드 하고,
     업로드 키 받아서 yaml 파일을 S3에서 읽어 메타정보 파싱 후,
     DB에 MapData저장하는 메서드
    */
    @Transactional
    public void handleMapFileUpload(String orinId, String measuredAt, MultipartFile yamlFile, MultipartFile pgmFile) throws IOException {
        // 1. S3에 파일 업로드해 키 받기
        String yamlKey = awsS3Service.uploadFile(orinId, yamlFile);
        String pgmKey = awsS3Service.uploadFile(orinId, pgmFile);

        // 2. S3에서 yaml 파일 직접 다운로드(버퍼)
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsS3Service.getBucketName())
                .key(yamlKey)
                .build();

        byte[] yamlBytes = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();

        // 3. yaml 파싱 (SnakeYAML 라이브러리 필요, MapMetaInfo 반환)
        MapMetaInfo metaInfo = parseYamlMeta(yamlBytes);

        // 4. device 조회 or 생성
        Device device = deviceRepository.findById(orinId)
                .orElseGet(() -> deviceRepository.save(new Device(orinId)));

        // 5. String -> LocalDateTime 변환
        LocalDateTime at = LocalDateTime.parse(measuredAt);

        // 6. 엔티티 생성 및 필드 채우기
        MapData mapData = new MapData();
        mapData.setDevice(device);
        mapData.setMeasuredAt(at);
        mapData.setPgmPath(pgmKey);
        mapData.setYamlPath(yamlKey);
        mapData.setResolution(metaInfo.getResolution());
        mapData.setOriginX(metaInfo.getOriginX());
        mapData.setOriginY(metaInfo.getOriginY());
        mapData.setOriginTheta(metaInfo.getOriginTheta());
        mapData.setOccupiedThresh(metaInfo.getOccupiedThresh());
        mapData.setFreeThresh(metaInfo.getFreeThresh());
        mapData.setNegate(metaInfo.getNegate());

        // 7. DB 저장
        mapDataRepository.save(mapData);
    }

    /**
     * yaml 파일 바이트 배열을 Map 객체로 읽어 MapMetaInfo DTO에 변환
     */
    private MapMetaInfo parseYamlMeta(byte[] yamlBytes) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream input = new ByteArrayInputStream(yamlBytes)) {
            Map<String, Object> data = yaml.load(input);
            MapMetaInfo meta = new MapMetaInfo();
            meta.setResolution(((Number)data.get("resolution")).floatValue());

            // origin 필드는 List로 하여 float 추출
            Object originRaw = data.get("origin");
            if (originRaw instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) originRaw;
                Float[] origin = new Float[3];
                int i=0;
                for(Object o : iterable) {
                    origin[i++] = ((Number)o).floatValue();
                    if (i>=3) break;
                }
                meta.setOriginX(origin[0]);
                meta.setOriginY(origin[1]);
                meta.setOriginTheta(origin[2]);
            }

            meta.setOccupiedThresh(((Number)data.get("occupied_thresh")).floatValue());
            meta.setFreeThresh(((Number)data.get("free_thresh")).floatValue());

            Object negateVal = data.get("negate");
            if (negateVal instanceof Boolean) {
                meta.setNegate((Boolean) negateVal);
            } else if (negateVal instanceof Number) {
                meta.setNegate(((Number)negateVal).intValue() != 0);
            }

            return meta;
        }
    }


    @Transactional
    public void saveMapData(MapDataRequest request){
        // 1. orinId 값으로 device 앤티티 검색. 없으면 새 device 객체 만들어 저장
        Device device = deviceRepository.findById(request.getOrinId())
                .orElseGet(() -> deviceRepository.save(new Device(request.getOrinId())));

        // 2. 측정 일시 문자열(ISO8601 형식)을 LocalDateTime 객체로 변환
        LocalDateTime measuredAt = LocalDateTime.parse(request.getMeasuredAt());

        // 3. 위의 정보 바탕으로 MapData 앤티티 객체 생성
        MapData mapData = new MapData();

        mapDataRepository.save(mapData);
    }

    // 최신 지도 메타정보 + presigned URL 제공 메서드 예시 (추가 가능)
    public MapViewResponse getLatestMapViewByDevice(String orinId) {
        MapData mapData = mapDataRepository.findTopByDeviceOrinIdOrderByMeasuredAtDesc(orinId)
                .orElseThrow(() -> new RuntimeException("No map data found"));

        // presigned URL 생성 (유효기간 10분)
        String pgmUrl = awsS3Service.generatePresignedUrl(mapData.getPgmPath(), Duration.ofMinutes(10));
        String yamlUrl = awsS3Service.generatePresignedUrl(mapData.getYamlPath(), Duration.ofMinutes(10));

        // 프론트에 반환할 DTO 생성
        return new MapViewResponse(
                pgmUrl,
                yamlUrl,
                mapData.getResolution(),
                mapData.getOriginX(),
                mapData.getOriginY(),
                mapData.getOriginTheta(),
                mapData.getOccupiedThresh(),
                mapData.getFreeThresh(),
                mapData.getNegate()
        );
    }

    // 오린카의 가장 최신 지도 JSON 데이터를 조회하는 메서드
//    public String getLatestMapArrayByDevice(String orinId){
//        return mapDataRepository.findToByDeviceOrinIdOrderByMeasuredAtDesc(orinId)
//                .map(MapData::getMapArray) // MapData 객체에서 getMapArray() 메서드를 호출해 mapArray 꺼내기
//                .orElse(null);
//    }
}

