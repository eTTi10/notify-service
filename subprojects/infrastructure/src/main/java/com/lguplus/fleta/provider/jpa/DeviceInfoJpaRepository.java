package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.entity.DeviceInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceInfoJpaRepository extends JpaRepository<DeviceInfoEntity, String> {
}
