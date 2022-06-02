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
        if (!checkNotiType(deviceInfoRequestDto)) {
            throw new BadRequestException("유효하지 않은 noti_type입니다.");
        }
        deviceInfoRepository.save(deviceInfoRequestDto);
    }

//  A: 전체받기, S: 구독만 받기, N: 푸시 안받기
    public boolean checkNotiType(DeviceInfoRequestDto deviceInfoRequestDto){
        if (deviceInfoRequestDto.getNotiType() == null) return false;
        else if (deviceInfoRequestDto.getNotiType().equals(NotiType.A.name())) return true;
        else if (deviceInfoRequestDto.getNotiType().equals(NotiType.N.name())) return true;
        else if (deviceInfoRequestDto.getNotiType().equals(NotiType.S.name())) return true;
        else return false;
    }

    public enum NotiType{
       A , N , S;
    }
}
