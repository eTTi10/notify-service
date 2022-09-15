package com.lguplus.fleta.domain.repository;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.entity.DeviceInfo;
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
    public DeviceInfo save(DeviceInfoRequestDto deviceInfoRequestDto) {
        return deviceInfoJpaRepository.save(DeviceInfo.builder()
            .saId(deviceInfoRequestDto.getSaId())
            .agentType(deviceInfoRequestDto.getAgentType())
            .serviceType(deviceInfoRequestDto.getServiceType())
            .notiType(deviceInfoRequestDto.getNotiType())
            .build());
    }

    @Override
    public void  delete(DeviceInfoRequestDto deviceInfoRequestDto){
        deviceInfoJpaRepository.delete(DeviceInfo.builder()
           .saId(deviceInfoRequestDto.getSaId())
           .agentType(deviceInfoRequestDto.getAgentType())
           .serviceType(deviceInfoRequestDto.getServiceType())
           .build());
   }

    @Override
    public boolean exist(DeviceInfoRequestDto deviceInfoRequestDto) {
        return deviceInfoJpaRepository.existsBySaIdAndAgentTypeAndServiceType(
            deviceInfoRequestDto.getSaId(),
            deviceInfoRequestDto.getAgentType(),
            deviceInfoRequestDto.getServiceType()
        );
    }
}
