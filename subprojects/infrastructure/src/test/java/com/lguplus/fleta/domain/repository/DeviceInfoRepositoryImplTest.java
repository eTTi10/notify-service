package com.lguplus.fleta.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.lguplus.fleta.data.dto.request.inner.HttpPushRequestDto;
import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.dto.response.inner.DeviceInfosResponseDto;
import com.lguplus.fleta.data.entity.DeviceInfo;
import com.lguplus.fleta.provider.jpa.DeviceInfoJpaRepository;
import com.lguplus.fleta.repository.push.DeviceInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
@ExtendWith(SpringExtension.class)
class DeviceInfoRepositoryImplTest {

    @Mock
    DeviceInfoRepository deviceInfoRepository;
    @Mock
    DeviceInfoJpaRepository deviceInfoJpaRepository;
    @InjectMocks
    DeviceInfoRepositoryImpl deviceInfoRepositoryImpl;

    @Test
    void save() {
        DeviceInfoRequestDto deviceInfoRequestDto = DeviceInfoRequestDto.builder()
            .saId("test1234")
            .serviceType("H")
            .agentType("G")
            .notiType("A")
            .build();
        assertDoesNotThrow(()->deviceInfoRepositoryImpl.save(deviceInfoRequestDto));
    }

    @Test
    void delete() {
        DeviceInfoRequestDto deviceInfoRequestDto = DeviceInfoRequestDto.builder()
            .saId("test1234")
            .serviceType("H")
            .agentType("G")
            .notiType("A")
            .build();
        assertDoesNotThrow(()->deviceInfoRepositoryImpl.delete(deviceInfoRequestDto));
    }

    @Test
    void exist() {
        given(deviceInfoJpaRepository.existsBySaIdAndAgentTypeAndServiceType(any(), any(), any()))
            .willReturn(true);
        DeviceInfoRequestDto deviceInfoRequestDto = DeviceInfoRequestDto.builder()
            .saId("test1234")
            .serviceType("H")
            .agentType("G")
            .notiType("A")
            .build();
        assertDoesNotThrow(()->deviceInfoRepositoryImpl.exist(deviceInfoRequestDto));
    }

    @Test
    @DisplayName("단말 정보 조회")
    void getDeviceInfos() {
        HttpPushRequestDto deviceInfosRequestDto = HttpPushRequestDto.builder()
                .saId("1000000871")
                .serviceType("H")
                .build();

        List<DeviceInfo> deviceInfos = new ArrayList<>();
        DeviceInfo deviceInfo = DeviceInfo.builder()
                .saId("1000000871")
                .agentType("G")
                .notiType("")
                .serviceType("H")
                .build();

        DeviceInfo deviceInfo1 = DeviceInfo.builder()
                .saId("1000000871")
                .agentType("A")
                .notiType("")
                .serviceType("H")
                .build();
        deviceInfos.add(deviceInfo);
        deviceInfos.add(deviceInfo1);


        given(deviceInfoJpaRepository.findBySaIdAndServiceType(any(), any())).willReturn(Optional.of(deviceInfos));

        List<DeviceInfosResponseDto> deviceInfosResponseDto = deviceInfoRepositoryImpl.getDeviceInfos(deviceInfosRequestDto);

        assertThat(deviceInfosResponseDto.size()).isSameAs(2);
    }

}