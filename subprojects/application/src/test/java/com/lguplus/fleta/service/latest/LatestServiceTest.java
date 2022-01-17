package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@Slf4j
class LatestServiceTest {
    @Mock
    LatestDomainService latestDomainService;

    @InjectMocks
    LatestService latestService;

    //####################### Start 알림조회 ######################
    @BeforeEach
    void getLatestListBefore() {

        LatestEntity rs1 = LatestEntity.builder()
                        .saId("500058151453")
                        .mac("001c.627e.039c")
                        .ctn("01055805424")
                        .catId("T3021")
                        .regId("500023630832")
                        .catName("놀라운 대회 스타킹")
                        .rDate(new Date())
                        .categoryGb("")
                        .build();

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
        log.info("LatestServiceTest.getLatestList End");
    }
    //####################### End 알림조회 ######################


    //####################### Start 알림삭제 ######################

    @Test
    @DisplayName("LatestServiceTest.deleteLatest 정상적으로 리스트 데이터를 삭제하는지 확인")
    void deleteLatest() {
        given(latestDomainService.deleteLatest(any())).willReturn(1);

        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500023630832")
                .mac("001c.6284.30a4")
                .ctn("01080808526")
                .catId("M0241").build();

        int resultCnt = latestService.deleteLatest(latestRequestDto);
        log.info("삭제테스트-resultCnt:"+resultCnt);
        Assertions.assertTrue(1 == resultCnt);
        log.info("LatestServiceTest.deleteLatest End");
    }
    //####################### End 알림삭제 ######################




    //####################### Start 알림등록 ######################

    @Test
    @DisplayName("LatestControllerTest.insertLatest 정상적으로 데이터를 등록하는지 확인")
    void insertLatest() {


        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500023630832")
                .mac("001c.6284.30a4")
                .ctn("01080808526")
                .catId("M0241")
                .regId("500023630832")
                .catName("신 삼국지 32회")
                .rDate("2014-10-27 16:19:38.000")
                .categoryGb("").build();

        latestService.insertLatest(latestRequestDto);

        log.info("LatestServiceTest.insertLatest End");

    }
    //####################### End 알림등록 ######################
}