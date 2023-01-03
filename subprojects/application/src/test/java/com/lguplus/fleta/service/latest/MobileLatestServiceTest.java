package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.request.outer.MobileLatestRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MobileLatestServiceTest {

    @Mock
    private MobileLatestDomainService mobileLatestDomainService;

    @InjectMocks
    private MobileLatestService mobileLatestService;

    @Test
    @Order(1)
    @DisplayName("1. 모바일 최신회차 알림 등록 테스트")
    void testInsertLatest() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .build();

        assertDoesNotThrow(() -> mobileLatestDomainService.insertLatest(requestDto));
    }

    @Test
    @Order(2)
    @DisplayName("2. 모바일 최신회차 알림 삭제 테스트")
    void testDeleteLatest() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .build();

        assertDoesNotThrow(() -> mobileLatestDomainService.deleteLatest(requestDto));
    }
}