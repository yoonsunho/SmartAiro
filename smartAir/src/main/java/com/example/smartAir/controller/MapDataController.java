package com.example.smartAir.controller;

import com.example.smartAir.dto.MapViewResponse;
import com.example.smartAir.service.MapDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * 지도 데이터 조회용 컨트롤러
 * - 최신 지도 메타정보와 S3 presigned URL 전달
 */
@RestController
@RequestMapping("/api/map-data")
public class MapDataController {

    private final MapDataService mapDataService;

    public MapDataController(MapDataService mapDataService) {
        this.mapDataService = mapDataService;
    }

    /**
     * 특정 orinId의 최신 지도 데이터를 JSON으로 응답
     * - 지도 이미지 접근용 presigned URL 포함
     * - 메타데이터는 DB에 저장된 값을 JSON으로 바로 내려줌
     */
    @GetMapping("/{orinId}/latest")
    public ResponseEntity<MapViewResponse> getLatestMapView(@PathVariable String orinId) {
        MapViewResponse response = mapDataService.getLatestMapViewByDevice(orinId);
        return ResponseEntity.ok(response);
    }
}
