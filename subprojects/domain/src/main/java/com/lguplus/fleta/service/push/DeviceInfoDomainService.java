package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.exception.latest.DeleteNotFoundException;
import com.lguplus.fleta.exception.push.NotFoundException;
import com.lguplus.fleta.repository.push.DeviceInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeviceInfoDomainService {
    private final DeviceInfoRepository deviceInfoRepository;

    public void createDeviceInfo(DeviceInfoRequestDto deviceInfoRequestDto){
        deviceInfoRepository.save(deviceInfoRequestDto);
    }

    public void deleteDeviceInfo(DeviceInfoRequestDto deviceInfoRequestDto){
        if (!deviceInfoRepository.exist(deviceInfoRequestDto)){
            throw new DeleteNotFoundException("DeviceInfoDomainService delete 대상 없음");
        }
        deviceInfoRepository.delete(deviceInfoRequestDto);
    }

    public void updateDeviceInfo(DeviceInfoRequestDto deviceInfoRequestDto){
        if (!deviceInfoRepository.exist(deviceInfoRequestDto)){
            throw new NotFoundException("DeviceInfoDomainService update 대상 없음");
        }
        deviceInfoRepository.save(deviceInfoRequestDto);
    }
}
