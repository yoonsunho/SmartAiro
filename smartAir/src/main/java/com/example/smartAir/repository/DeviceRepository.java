package com.example.smartAir.repository;

import com.example.smartAir.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

// 기본 CRUD 지원, Device 테이블 조작 인터페이스
public interface DeviceRepository extends JpaRepository<Device,String> {

}
