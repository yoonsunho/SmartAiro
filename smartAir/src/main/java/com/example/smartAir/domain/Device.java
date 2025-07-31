package com.example.smartAir.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // JPA Entity 어노테이션, 테이블로 매핑
@Getter     // Lombok 자동 getter 생성
@NoArgsConstructor  // 기본 생성자 자동 생성
public class Device {
    @Id //pk지정
    private String orinId;

    public Device(String orinId) {
        this.orinId = orinId;
    }
}
