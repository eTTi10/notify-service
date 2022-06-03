package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.exception.push.BadRequestException;
import com.lguplus.fleta.repository.push.DeviceInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeviceInfoDomainService {
    private final DeviceInfoRepository deviceInfoRepository;
    // 여기서는 왜 맵퍼를 안쓰는 지??
    public Long pushDeviceCnt(DeviceInfoRequestDto deviceInfoRequestDto){
        return deviceInfoRepository.countBySaIdAndAgentTypeAndServiceType(
            deviceInfoRequestDto.getSaId(),
            deviceInfoRequestDto.getAgentType(),
            deviceInfoRequestDto.getServiceType());
    }

    public void createDeviceInfo(DeviceInfoRequestDto deviceInfoRequestDto){
        deviceInfoRepository.save(deviceInfoRequestDto);
    }
}
