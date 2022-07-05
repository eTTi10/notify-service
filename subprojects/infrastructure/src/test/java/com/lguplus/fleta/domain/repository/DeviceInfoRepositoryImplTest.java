package com.lguplus.fleta.domain.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.provider.jpa.DeviceInfoJpaRepository;
import com.lguplus.fleta.repository.push.DeviceInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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

}