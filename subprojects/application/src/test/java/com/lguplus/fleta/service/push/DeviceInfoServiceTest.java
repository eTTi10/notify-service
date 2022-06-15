package com.lguplus.fleta.service.push;

import static org.junit.jupiter.api.Assertions.*;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class DeviceInfoServiceTest {

    @InjectMocks
    DeviceInfoService deviceInfoService;

    @Mock
    DeviceInfoDomainService deviceInfoDomainService;

    @Test
    void createDeviceInfo() {
        DeviceInfoRequestDto deviceInfoRequestDto = DeviceInfoRequestDto.builder()
            .saId("test1234")
            .serviceType("H")
            .agentType("G")
            .notiType("A")
            .build();
        assertDoesNotThrow(()->deviceInfoService.createDeviceInfo(deviceInfoRequestDto));
    }

    @Test
    void deleteDeviceInfo() {
        DeviceInfoRequestDto deviceInfoRequestDto = DeviceInfoRequestDto.builder()
            .saId("test1234")
            .serviceType("H")
            .agentType("G")
            .notiType("A")
            .build();
        assertDoesNotThrow(()->deviceInfoService.deleteDeviceInfo(deviceInfoRequestDto));
    }

    @Test
    void updateDeviceInfo() {
        DeviceInfoRequestDto deviceInfoRequestDto = DeviceInfoRequestDto.builder()
            .saId("test1234")
            .serviceType("H")
            .agentType("G")
            .notiType("A")
            .build();
        assertDoesNotThrow(()->deviceInfoService.updateDeviceInfo(deviceInfoRequestDto));
    }
}