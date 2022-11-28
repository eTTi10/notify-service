package com.lguplus.fleta.service.push;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.lguplus.fleta.data.dto.request.inner.HttpPushRequestDto;
import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.dto.response.inner.DeviceInfosResponseDto;
import com.lguplus.fleta.exception.NoResultException;
import com.lguplus.fleta.exception.database.DataNotExistsException;
import com.lguplus.fleta.repository.push.DeviceInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    @DisplayName("단말 정보 조회")
    void getDeviceInfos() {
        HttpPushRequestDto deviceInfosRequestDto = HttpPushRequestDto.builder()
                .saId("1000000871")
                .serviceType("H")
                .build();

        List<DeviceInfosResponseDto> deviceInfosResponseDto = new ArrayList<>();
        DeviceInfosResponseDto deviceInfo = DeviceInfosResponseDto.builder()
                .saId("1000000871")
                .agentType("G")
                .serviceType("H")
                .notiType("")
                .build();
        deviceInfosResponseDto.add(deviceInfo);

        given(deviceInfoRepository.getDeviceInfos(any())).willReturn(deviceInfosResponseDto);

        List<DeviceInfosResponseDto> deviceInfos = deviceInfoDomainService.getDeviceInfos(deviceInfosRequestDto);

        assertThat(deviceInfos.get(0).getSaId()).isEqualTo("1000000871");
    }

}