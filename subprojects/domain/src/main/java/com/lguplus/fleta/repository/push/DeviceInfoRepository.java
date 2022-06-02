package com.lguplus.fleta.repository.push;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.entity.DeviceInfoEntity;

public interface DeviceInfoRepository {
    long countBySaIdAndAgentTypeAndServiceType(String saId, String AgentType, String serviceType);
    DeviceInfoEntity save(DeviceInfoRequestDto deviceInfoEntity);
}

