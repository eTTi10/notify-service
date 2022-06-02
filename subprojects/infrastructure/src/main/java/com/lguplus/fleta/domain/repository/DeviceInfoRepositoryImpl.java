package com.lguplus.fleta.domain.repository;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.entity.DeviceInfoEntity;
import com.lguplus.fleta.provider.jpa.DeviceInfoJpaRepository;
import com.lguplus.fleta.repository.push.DeviceInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DeviceInfoRepositoryImpl implements DeviceInfoRepository {

    private final DeviceInfoJpaRepository deviceInfoJpaRepository;
    @Override
    public long countBySaIdAndAgentTypeAndServiceType(String saId, String AgentType, String serviceType) {
        return deviceInfoJpaRepository.countBySaIdAndAgentTypeAndServiceType(saId, AgentType, serviceType);
    }

    @Override
    public DeviceInfoEntity save(DeviceInfoRequestDto deviceInfoRequestDto) {
        return deviceInfoJpaRepository.save(DeviceInfoEntity.builder()
            .saId(deviceInfoRequestDto.getSaId())
            .agentType(deviceInfoRequestDto.getAgentType())
            .serviceType(deviceInfoRequestDto.getServiceType())
            .notiType(deviceInfoRequestDto.getNotiType())
            .build());
    }

}
