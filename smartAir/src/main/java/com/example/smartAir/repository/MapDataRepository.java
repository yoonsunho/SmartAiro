package com.example.smartAir.repository;

import com.example.smartAir.domain.MapData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MapDataRepository extends JpaRepository<MapData, Long> {
    
    // 특정 장비(오린카)기준 가장 최신 데이터 한 건 조회
    Optional<MapData> findTopByDeviceOrinIdOrderByMeasuredAtDesc(String orinId);

}
