package com.example.smartAir.dto;

import com.example.smartAir.domain.MapData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

// 프론트 앤드에 지도 이미지 URL응답  및 메타정보(JSON 형태)를 전달할 때 쓰는 응답 DTO
@Getter
@AllArgsConstructor
public class MapViewResponse {
    private String pgmUrl;  // 지도 이미지 파일 접근 URL
    private String yamlUrl; // 메타 정보 YAML 파일 URL
    private Float resolution;
    private Float originX;
    private Float originY;
    private Float originTheta;
    private Float occupiedThresh;
    private Float freeThresh;
    private Boolean negate;

    public MapViewResponse(MapData entity){
        String orinId = entity.getDevice().getOrinId();
        String pgmFile = new File(entity.getPgmPath()).getName();
        String yamlFile = new File(entity.getYamlPath()).getName();

        // 클라이언트가 접근할 수 있게 API 경로로 URL 구성
        this.pgmUrl = "/api/map-view/image/" + orinId + "/"+ pgmFile;
        this.yamlUrl = "/api/map-view/yaml/"+ orinId + "/" + yamlFile;

        this.resolution = entity.getResolution();
        this.originX = entity.getOriginX();
        this.originY = entity.getOriginY();
        this.originTheta = entity.getOriginTheta();
        this.occupiedThresh = entity.getOccupiedThresh();
        this.freeThresh = entity.getFreeThresh();
        this.negate = entity.getNegate();

    }
}
