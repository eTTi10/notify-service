package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.entity.DeviceInfo;
import com.lguplus.fleta.data.entity.id.DeviceInfoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceInfoJpaRepository extends JpaRepository<DeviceInfo, DeviceInfoId> {
    boolean existsBySaIdAndAgentTypeAndServiceType(String saId, String agentType, String serviceType);
}
