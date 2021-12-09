package com.lguplus.fleta.repository;


import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.data.mapper.LatestMapper;
import com.lguplus.fleta.service.latest.LatestDomainService;
import com.lguplus.fleta.util.JunitTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LatestRepositoryTest {

    @InjectMocks
    LatestDomainService latestDomainService;

    @Mock
    LatestMapper latestMapper;

    @Mock
    LatestRepository latestRepository;

    @BeforeEach
    void setUp() {
        latestDomainService = new LatestDomainService(latestMapper, latestRepository);
    }

    @Test
    @DisplayName("LatestRepositoryTest.getLatestList 정상적으로 리스트 데이터를 수신하는지 확인")
    void getLatestList() {
        LatestEntity rs1 = new LatestEntity();
        JunitTestUtils.setValue(rs1, "saId", "500058151453");
        JunitTestUtils.setValue(rs1, "mac", "001c.627e.039c");
        JunitTestUtils.setValue(rs1, "ctn", "01055805424");
        JunitTestUtils.setValue(rs1, "catId", "T3021");
        JunitTestUtils.setValue(rs1, "regId", "500023630832");
        JunitTestUtils.setValue(rs1, "catName", "놀라운 대회 스타킹");
        JunitTestUtils.setValue(rs1, "rDate", "2014-11-09 13:18:14.000");
        JunitTestUtils.setValue(rs1, "categoryGb", "");

        List<LatestEntity> list = List.of(rs1);

        List<LatestDto> resultList = new ArrayList<LatestDto>();
        list.forEach(e->{
            LatestDto item = latestMapper.toDto(e);
            resultList.add(item);
        });

        // Mock Method
        given(latestDomainService.getLatestList(any())).willReturn(resultList);

        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021").build();

        List<LatestDto> responseList = latestDomainService.getLatestList(latestRequestDto);
        assertThat(responseList.size()).isEqualTo(resultList.size()); // mock 의 결과 size 와 메소드 실행 결과 사이즈가 같은지 확인

        log.info("LatestRepositoryTest.getLatestList End");
    }
}