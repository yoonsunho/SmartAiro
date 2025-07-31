package com.example.smartAir.service;

import com.example.smartAir.domain.Device;
import com.example.smartAir.domain.SensorData;
import com.example.smartAir.dto.SensorDataRequest;
import com.example.smartAir.repository.DeviceRepository;
import com.example.smartAir.repository.SensorDataRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Service  // Service 레이어 빈 등록
public class SensorDataService {

    private final DeviceRepository deviceRepository;
    private final SensorDataRepository sensorDataRepository;

    // 의존성 주입
    public SensorDataService(DeviceRepository deviceRepository, SensorDataRepository sensorDataRepository) {
        this.deviceRepository = deviceRepository;
        this.sensorDataRepository = sensorDataRepository;
    }

    @Transactional
    public SensorData saveSensorData(SensorDataRequest request){
        // 1. Device 존재 여부 확인 후 없으면 저장
        Device device = deviceRepository.findById(request.getOrinId())
                .orElseGet(() -> deviceRepository.save(new Device(request.getOrinId())));

        // 2. 입력받은 measuredAt 문자열을 LocalDateTime으로 변환
        LocalDateTime measuredAt;
        try{
            measuredAt = LocalDateTime.parse(request.getMeasuredAt());
        }catch (DateTimeParseException e){
            // 파싱 실패 시 현재 시간으로 대체 (필요 시 예외 처리로 변경 가능)
            measuredAt = LocalDateTime.now();
        }

        // 3. SensorData 엔티티 생성 후 저장
        SensorData sensorData = new SensorData(
                device,
                measuredAt,
                request.getTemperature(),
                request.getHumidity(),
                request.getAirQuality(),
                request.getCoordX(),
                request.getCoordY(),
                request.getRoom()
        );

        return sensorDataRepository.save(sensorData);   // DB 저장 및 저장된 엔티티 반환
    }


}
