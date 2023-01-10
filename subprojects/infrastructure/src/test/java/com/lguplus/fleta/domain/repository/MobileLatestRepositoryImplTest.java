package com.lguplus.fleta.domain.repository;

import com.lguplus.fleta.data.dto.request.outer.MobileLatestRequestDto;
import com.lguplus.fleta.data.entity.MobileLatest;
import com.lguplus.fleta.provider.jpa.MobileLatestJpaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class MobileLatestRepositoryImplTest {

    @Mock
    private MobileLatestJpaRepository mobileLatestJpaRepository;

    @InjectMocks
    private MobileLatestRepositoryImpl mobileLatestRepositoryImpl;

    @Test
    void insertLatestTest() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .categoryId("T3021")
            .registrantId("500058151453")
            .categoryName("놀라운 대회 스타킹")
            .serviceType("V")
            .build();

        MobileLatest entity = MobileLatest.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .catId("T3021")
            .regId("500058151453")
            .catName("놀라운 대회 스타킹")
            .serviceType("V")
            .build();

        given(mobileLatestJpaRepository.saveAndFlush(any())).willReturn(entity);

        MobileLatest resultEntity = mobileLatestRepositoryImpl.insertLatest(requestDto);

        assertEquals("T3021", resultEntity.getCatId());
    }

    @Test
    void deleteLatestTest() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .categoryId("T3021")
            .registrantId("500058151453")
            .categoryName("놀라운 대회 스타킹")
            .serviceType("V")
            .build();

        given(mobileLatestJpaRepository.deleteLatest(any())).willReturn(1);

        int result = mobileLatestRepositoryImpl.deleteLatest(requestDto);

        assertEquals(1, result);
    }

    @Test
    void getLatestCountListTest() {

        MobileLatestRequestDto requestDto = MobileLatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01012345678")
            .categoryId("T3021")
            .registrantId("500058151453")
            .categoryName("놀라운 대회 스타킹")
            .serviceType("V")
            .build();

        given(mobileLatestJpaRepository.getLatestCountList(any())).willReturn(List.of("T3021"));

        List<String> resultList = mobileLatestRepositoryImpl.getLatestCountList(requestDto);

        assertEquals(1, resultList.size());
    }
}