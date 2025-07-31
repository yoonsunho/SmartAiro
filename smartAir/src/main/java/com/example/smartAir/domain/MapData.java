package com.example.smartAir.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MapData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mapId;  // PK: 지도 데이터 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)  // 하나의 오린카에 여러 맵 데이터 생길 수 있음..
    @JoinColumn(name = "orin_id")
    private Device device;

    private LocalDateTime measuredAt;   // 지도(맵) 수신/생성 시각

    private String pgmPath;     //// PGM 이미지 파일 저장 경로 (로컬 또는 네트워크 경로)

    private String yamlPath;    // YAML 메타데이터 파일 저장 경로

    private Float resolution;       // YAML 파일에서 파싱한 해상도 (맵 1픽셀이 실제 몇 미터에 해당하는지)

    @Column(name = "origin_x")
    private Float originX;      // 맵 좌표계 원점 X 좌표 (실제 세계 좌표 기준, 미터 단위)

    @Column(name = "origin_y")
    private Float originY;      // 맵 좌표계 원점 Y 좌표 (실제 세계 좌표 기준, 미터 단위)

    private Float originTheta;  // 원점 기준 회전 각도 (radian 단위)

    private Float occupiedThresh;   // 점유 임계값 (해당 픽셀이 벽/장애물로 간주되는 기준값)

    private Float freeThresh;   // 자유 공간 임계값 (해당 픽셀이 빈 공간으로 간주되는 기준값)

    private Boolean negate;     // 이미지 색상 반전 여부 (true면 색상 반전 적용)

    public MapData(Device device, LocalDateTime measuredAt) {
        this.device = device;
        this.measuredAt = measuredAt;
    }
}
