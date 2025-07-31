package com.example.smartAir.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorDataRequest {
    @NotNull // 반드시 포함
    private String orinId; //장비 ID

    @NotNull
    private String measuredAt;  //// ISO8601 형태 날짜 문자열 (ex: "2025-07-24T15:00:00")

    // 센서에서 받은 수치들
    private Float temperature;
    private Float humidity;
    private Float airQuality;

    private Float coordX;
    private Float coordY;
    private String room;

}
