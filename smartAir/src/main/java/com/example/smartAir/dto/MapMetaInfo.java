package com.example.smartAir.dto;

import lombok.Getter;
import lombok.Setter;
// 백에서 yaml 파일을 파싱한 지도 메타 데이터(해상도, 원점좌표, 임계치 등)을 담는 DTO
// yaml 메타 파일 파싱 결과 보관, 서비스 내부용 DTO
@Getter
@Setter
public class MapMetaInfo {

    private Float resolution;      // 해상도
    private Float originX;         // 원점 X 좌표
    private Float originY;         // 원점 Y 좌표
    private Float originTheta;     // 회전 각도
    private Float occupiedThresh;  // 점유 임계값
    private Float freeThresh;      // 자유 공간 임계값
    private Boolean negate;        // 색상 반전 여부

}
