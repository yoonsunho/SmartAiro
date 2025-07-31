package com.example.smartAir.dto;

import lombok.Getter;
import lombok.Setter;

// 임베디드에서 들어오는 업로드 요청 정보 수신용 DTO(최소한의 식별정보)

@Getter
@Setter
public class MapDataRequest {  // 임베디드에서 백엔드로 지도 데이터를 업로드할 때 받는 기본 요청 정보(메타정보나 파일 업로드 외 필수값)용 DTO

    private String orinId;
    private String measuredAt;  // ISO 8601 형식 문자열, 예: "2025-07-24T15:00:00"


}
