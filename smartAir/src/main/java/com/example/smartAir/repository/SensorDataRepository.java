package com.example.smartAir.repository;

import com.example.smartAir.domain.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;

// SensorData 테이블 조작용 JpaRepository
public interface SensorDataRepository extends JpaRepository<SensorData,Long> {

}
