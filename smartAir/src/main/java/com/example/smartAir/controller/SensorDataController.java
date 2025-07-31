package com.example.smartAir.controller;

import com.example.smartAir.domain.SensorData;
import com.example.smartAir.dto.SensorDataRequest;
import com.example.smartAir.service.SensorDataService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // REST 컨트롤러 스프링 빈 등록
@RequestMapping("/api/sensor-data")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    // 서비스 주입
    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    // post 요청 시 JSON 바디를 SensorDataRequest 로 받고 저장
    @PostMapping
    public ResponseEntity<String> receiveSensorData(@Valid @RequestBody SensorDataRequest request){
        SensorData saved = sensorDataService.saveSensorData(request);
        // 저장된 데이터의 ID를 반환
        return ResponseEntity.ok("Data saved with id" + saved.getDataId());

    }
}
