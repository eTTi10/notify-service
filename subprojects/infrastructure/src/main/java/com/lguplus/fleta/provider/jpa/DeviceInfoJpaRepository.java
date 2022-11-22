package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.entity.DeviceInfo;
import com.lguplus.fleta.data.entity.id.DeviceInfoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceInfoJpaRepository extends JpaRepository<DeviceInfo, DeviceInfoId> {
    boolean existsBySaIdAndAgentTypeAndServiceType(String saId, String agentType, String serviceType);
    Optional<List<DeviceInfo>> findBySaIdAndServiceType(String saId, String serviceType);
}
