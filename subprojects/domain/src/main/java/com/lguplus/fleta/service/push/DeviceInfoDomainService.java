package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.inner.HttpPushRequestDto;
import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.dto.response.inner.DeviceInfosResponseDto;
import com.lguplus.fleta.exception.NoResultException;
import com.lguplus.fleta.exception.database.DataNotExistsException;
import com.lguplus.fleta.repository.push.DeviceInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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
            throw new DataNotExistsException("데이터 미존재");
        }
        deviceInfoRepository.delete(deviceInfoRequestDto);
    }

    public void updateDeviceInfo(DeviceInfoRequestDto deviceInfoRequestDto){
        if (!deviceInfoRepository.exist(deviceInfoRequestDto)){
           throw new NoResultException("데이터 미존재");
        }
        deviceInfoRepository.save(deviceInfoRequestDto);
    }

    public List<DeviceInfosResponseDto> getDeviceInfos(HttpPushRequestDto deviceInfosRequestDto){
        return deviceInfoRepository.getDeviceInfos(deviceInfosRequestDto);
    }

}
