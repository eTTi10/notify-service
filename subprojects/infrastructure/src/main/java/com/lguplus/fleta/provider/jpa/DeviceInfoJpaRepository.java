package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.entity.DeviceInfoEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceInfoJpaRepository extends JpaRepository<DeviceInfoEntity, String> {
    long countBySaIdAndAgentTypeAndServiceType(String saId, String AgentType, String serviceType);
}
