package com.example.smartAir.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dataId;

    // Device 엔티티와 ManyToOne 관계. (하나의 device에 여러 sensordata 속함)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orinId")    // 외래키 컬럼명
    private Device device;

    private LocalDateTime measuredAt; // 측정 시각
    private Float temperature;  //온도
    private Float humidity;     // 습도
    private Float airQuality;   // 공기질

    @Column(name = "coord_x")
    private Float coordX;       // 측정 x좌표
    @Column(name="coord_y")
    private Float coordY;       // 측정 y좌표

    private String room;        // 측정 방 이름

    public SensorData(Device device, LocalDateTime measuredAt, Float temperature, Float humidity, Float airQuality, Float coordX, Float coordY, String room) {

        this.device = device;
        this.measuredAt = measuredAt;
        this.temperature = temperature;
        this.humidity = humidity;
        this.airQuality = airQuality;
        this.coordX = coordX;
        this.coordY = coordY;
        this.room = room;
    }
}
