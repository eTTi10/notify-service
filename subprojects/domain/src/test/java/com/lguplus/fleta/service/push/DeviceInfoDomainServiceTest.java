package com.lguplus.fleta.service.push;

import static org.junit.jupiter.api.Assertions.*;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.exception.NoResultException;
import com.lguplus.fleta.exception.database.DataNotExistsException;
import com.lguplus.fleta.exception.latest.DeleteNotFoundException;
import com.lguplus.fleta.exception.push.NotFoundException;
import com.lguplus.fleta.repository.push.DeviceInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeviceInfoDomainServiceTest {

    @Mock
    DeviceInfoRepository deviceInfoRepository;

    @InjectMocks
    DeviceInfoDomainService deviceInfoDomainService;

    @Test
    void createDeviceInfo() {
        DeviceInfoRequestDto deviceInfoRequestDto = DeviceInfoRequestDto.builder()
            .saId("test1234")
            .serviceType("H")
            .agentType("G")
            .notiType("A")
            .build();

        assertDoesNotThrow(()->deviceInfoDomainService.createDeviceInfo(deviceInfoRequestDto));
    }

    @Test
    void deleteDeviceInfo() {
        given(deviceInfoRepository.exist(any())).willReturn(true);
        DeviceInfoRequestDto deviceInfoRequestDto = DeviceInfoRequestDto.builder()
            .saId("test1234")
            .serviceType("H")
            .agentType("G")
            .notiType("A")
            .build();

        assertDoesNotThrow(()->deviceInfoDomainService.deleteDeviceInfo(deviceInfoRequestDto));
    }

    @Test
    void updateDeviceInfo() {
        given(deviceInfoRepository.exist(any())).willReturn(true);
        DeviceInfoRequestDto deviceInfoRequestDto = DeviceInfoRequestDto.builder()
            .saId("test1234")
            .serviceType("H")
            .agentType("G")
            .notiType("A")
            .build();

        assertDoesNotThrow(()->deviceInfoDomainService.updateDeviceInfo(deviceInfoRequestDto));
    }


    @Test
    void deleteDeviceInfoThrow() {
        given(deviceInfoRepository.exist(any())).willReturn(false);
        DeviceInfoRequestDto deviceInfoRequestDto = DeviceInfoRequestDto.builder()
            .saId("test1234")
            .serviceType("H")
            .agentType("G")
            .notiType("A")
            .build();
        assertThrows(DataNotExistsException.class,()-> deviceInfoDomainService.deleteDeviceInfo(deviceInfoRequestDto));
    }

    @Test
    void updateDeviceInfoThrow() {
        given(deviceInfoRepository.exist(any())).willReturn(false);
        DeviceInfoRequestDto deviceInfoRequestDto = DeviceInfoRequestDto.builder()
            .saId("test1234")
            .serviceType("H")
            .agentType("G")
            .notiType("A")
            .build();

        assertThrows(NoResultException.class,()-> deviceInfoDomainService.updateDeviceInfo(deviceInfoRequestDto));
    }
}