package com.example.smartAir.controller;

import com.example.smartAir.service.MapDataService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 지도 데이터파일(.pgm, .yaml) 업로드 전용 컨트롤러
 * - 멀티파트 요청을 받아 파일을 S3에 업로드 및 DB 저장 호출
 */
@RestController
@RequestMapping("/api/map-upload")
public class MapUploadController {

    private final MapDataService mapDataService;

    public MapUploadController(MapDataService mapDataService) {
        this.mapDataService = mapDataService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadMapFiles(
            @RequestParam String orinId,
            @RequestParam String measuredAt,
            @RequestPart MultipartFile yamlFile,
            @RequestPart MultipartFile pgmFile) throws IOException {

        // 서비스에 파일 업로드 및 DB 저장 로직 위임
        mapDataService.handleMapFileUpload(orinId, measuredAt, yamlFile, pgmFile);

        return ResponseEntity.ok("파일 업로드 및 저장 완료");
    }
}
