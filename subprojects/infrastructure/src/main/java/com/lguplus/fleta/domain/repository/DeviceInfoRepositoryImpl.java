package com.lguplus.fleta.domain.repository;

import com.lguplus.fleta.data.dto.request.inner.HttpPushRequestDto;
import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.dto.response.inner.DeviceInfosResponseDto;
import com.lguplus.fleta.data.entity.DeviceInfo;
import com.lguplus.fleta.provider.jpa.DeviceInfoJpaRepository;
import com.lguplus.fleta.repository.push.DeviceInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DeviceInfoRepositoryImpl implements DeviceInfoRepository {

    private final DeviceInfoJpaRepository deviceInfoJpaRepository;

    @Override
    public DeviceInfo save(DeviceInfoRequestDto deviceInfoRequestDto) {
        return deviceInfoJpaRepository.saveAndFlush(DeviceInfo.builder()
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

    @Override
    public List<DeviceInfosResponseDto> getDeviceInfos(HttpPushRequestDto deviceInfosRequestDto) {
        return deviceInfoJpaRepository.findBySaIdAndServiceType(deviceInfosRequestDto.getSaId(), deviceInfosRequestDto.getServiceType())
                .orElseGet(Collections::emptyList)
                .stream()
                .map(DeviceInfosResponseDto::new)
                .collect(Collectors.toList());
    }

}
