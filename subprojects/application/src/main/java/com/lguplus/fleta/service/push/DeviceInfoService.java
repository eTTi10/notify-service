package com.lguplus.fleta.service.push;


import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.exception.UndefinedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceInfoService {
    private final DeviceInfoDomainService deviceInfoDomainService;

    @Transactional
    public void createDeviceInfo(DeviceInfoRequestDto deviceInfoRequestDto){
        try {
            deviceInfoDomainService.createDeviceInfo(deviceInfoRequestDto);
        } catch (final Exception e) {
            throw new UndefinedException("기타 오류");
        }
    }
    @Transactional
    public void deleteDeviceInfo(DeviceInfoRequestDto deviceInfoRequestDto){
        deviceInfoDomainService.deleteDeviceInfo(deviceInfoRequestDto);
    }
    @Transactional
    public void updateDeviceInfo(DeviceInfoRequestDto deviceInfoRequestDto){
        deviceInfoDomainService.updateDeviceInfo(deviceInfoRequestDto);
    }
}
