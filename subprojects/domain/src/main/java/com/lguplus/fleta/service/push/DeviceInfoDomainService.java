package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.exception.NoResultException;
import com.lguplus.fleta.exception.database.DataNotExistsException;
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
            throw new DataNotExistsException("DeviceInfoDomainService delete 대상 없음");
        }
        deviceInfoRepository.delete(deviceInfoRequestDto);
    }

    public void updateDeviceInfo(DeviceInfoRequestDto deviceInfoRequestDto){
        if (!deviceInfoRepository.exist(deviceInfoRequestDto)){
           throw new NoResultException("DeviceInfoDomainService update 대상 없음");
        }
        deviceInfoRepository.save(deviceInfoRequestDto);
    }
}
