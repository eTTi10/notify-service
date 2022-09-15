package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.entity.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceInfoJpaRepository extends JpaRepository<DeviceInfo, String> {
    boolean existsBySaIdAndAgentTypeAndServiceType(String saId, String agentType, String serviceType);
}
