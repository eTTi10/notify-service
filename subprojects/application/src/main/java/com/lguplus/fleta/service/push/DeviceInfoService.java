package com.lguplus.fleta.service.push;


import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceInfoService {
    private final DeviceInfoDomainService deviceInfoDomainService;

    @Transactional
    public void createDeviceInfo(DeviceInfoRequestDto deviceInfoRequestDto){
         deviceInfoDomainService.createDeviceInfo(deviceInfoRequestDto);
    }
}
