package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.request.outer.MobileLatestRequestDto;
import com.lguplus.fleta.exception.ExceedMaxRequestException;
import com.lguplus.fleta.exception.UndefinedException;
import com.lguplus.fleta.exception.database.DataAlreadyExistsException;
import com.lguplus.fleta.exception.latest.DeleteNotFoundException;
import com.lguplus.fleta.repository.latest.MobileLatestRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MobileLatestDomainServiceTest {

    @Mock
    private MobileLatestRepository mobileLatestRepository;

    @InjectMocks
    private MobileLatestDomainService mobileLatestDomainService;

    @Test
    @Order(1)
    void testInsertLatest() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .categoryId("T3021")
            .categoryName("놀라운 대회 스타킹")
            .registrantId("500058151453")
            .build();

        assertDoesNotThrow(() -> mobileLatestDomainService.insertLatest(requestDto));
    }

    @Test
    @Order(2)
    void testInsertLatest_emptyList() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .categoryId("T3021")
            .categoryName("놀라운 대회 스타킹")
            .registrantId("500058151453")
            .build();

        doReturn(Collections.emptyList()).when(mobileLatestRepository).getLatestCountList(any());

        assertDoesNotThrow(() -> mobileLatestDomainService.insertLatest(requestDto));
    }

    @Test
    @Order(3)
    void testInsertLatest_Exception() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .categoryId("T3021")
            .categoryName("놀라운 대회 스타킹")
            .registrantId("500058151453")
            .build();

        doThrow(new RuntimeException("기타 오류")).when(mobileLatestRepository).insertLatest(any());

        assertThrows(UndefinedException.class, () -> mobileLatestDomainService.insertLatest(requestDto));
    }

    @Test
    @Order(4)
    void testInsertLatest_ExceedMaxRequestException() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .categoryId("T3021")
            .categoryName("놀라운 대회 스타킹")
            .registrantId("500058151453")
            .build();

        doReturn(List.of("T3023", "T3024", "T3025", "T3026", "T3027")).when(mobileLatestRepository).getLatestCountList(any());

        assertThrows(ExceedMaxRequestException.class, () -> mobileLatestDomainService.insertLatest(requestDto));
    }

    @Test
    @Order(5)
    void testInsertLatest_DataAlreadyExistsException() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .categoryId("T3021")
            .categoryName("놀라운 대회 스타킹")
            .registrantId("500058151453")
            .build();

        doReturn(List.of("T3021")).when(mobileLatestRepository).getLatestCountList(any());

        assertThrows(DataAlreadyExistsException.class, () -> mobileLatestDomainService.insertLatest(requestDto));
    }

    @Test
    @Order(6)
    void testInsertLatest_NoException() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .categoryId("T3021")
            .categoryName("놀라운 대회 스타킹")
            .registrantId("500058151453")
            .build();

        doReturn(List.of("T3023")).when(mobileLatestRepository).getLatestCountList(any());

        assertDoesNotThrow(() -> mobileLatestDomainService.insertLatest(requestDto));
    }

    @Test
    @Order(7)
    void testDeleteLatest() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .categoryId("T3021")
            .categoryName("놀라운 대회 스타킹")
            .registrantId("500058151453")
            .build();

        doReturn(1).when(mobileLatestRepository).deleteLatest(any());

        assertDoesNotThrow(() -> mobileLatestDomainService.deleteLatest(requestDto));
    }

    @Test
    @Order(8)
    void testDeleteLatest_DeleteNotFoundException() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .categoryId("T3021")
            .categoryName("놀라운 대회 스타킹")
            .registrantId("500058151453")
            .build();

        doReturn(0).when(mobileLatestRepository).deleteLatest(any());

        assertThrows(DeleteNotFoundException.class, () ->
            mobileLatestDomainService.deleteLatest(requestDto));
    }
}