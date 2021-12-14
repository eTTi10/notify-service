package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.util.JunitTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@Slf4j
class LatestServiceTest {
    @Mock
    LatestDomainService latestDomainService;

    @InjectMocks
    LatestService latestService;

    @BeforeEach
    void getLatestListSetUp() {

        LatestEntity rs1 = new LatestEntity();
        JunitTestUtils.setValue(rs1, "saId", "500058151453");
        JunitTestUtils.setValue(rs1, "mac", "001c.627e.039c");
        JunitTestUtils.setValue(rs1, "ctn", "01055805424");
        JunitTestUtils.setValue(rs1, "catId", "T3021");
        JunitTestUtils.setValue(rs1, "regId", "500023630832");
        JunitTestUtils.setValue(rs1, "catName", "놀라운 대회 스타킹");
        JunitTestUtils.setValue(rs1, "rDate", "2014-11-09 13:18:14.000");
        JunitTestUtils.setValue(rs1, "categoryGb", "");

        List<LatestEntity> rs = List.of(rs1);
        List<LatestDto> resultList = new ArrayList<LatestDto>();

        rs.forEach(e -> {
            LatestDto item = LatestDto.builder()
                    .saId(e.getSaId())
                    .mac(e.getMac())
                    .ctn(e.getCtn())
                    .catId(e.getCatId())
                    .catName(e.getCatName())
                    .rDate(e.getRDate())
                    .categoryGb(e.getCategoryGb())
                    .build();
            resultList.add(item);
        });

        given(latestDomainService.getLatestList(any())).willReturn(resultList);
    }

    @Test
    @DisplayName("LatestServiceTest.getLatestList 정상적으로 리스트 데이터를 수신하는지 확인")
    void getLatestList() {
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021").build();

        String resultFlag = latestService.getLatestList(latestRequestDto).getFlag();
        log.info(latestService.getLatestList(latestRequestDto).getMessage());
        Assertions.assertTrue("0000".equals(resultFlag));
        log.info("LatestServiceTest End");
    }
}