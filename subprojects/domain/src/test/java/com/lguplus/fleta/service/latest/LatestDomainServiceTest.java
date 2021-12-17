package com.lguplus.fleta.repository;

import com.lguplus.fleta.data.dto.LatestCheckDto;
import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestCheckEntity;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.data.mapper.LatestMapper;
import com.lguplus.fleta.exception.ExceedMaxRequestException;
import com.lguplus.fleta.exception.database.DatabaseException;
import com.lguplus.fleta.exception.database.DuplicateKeyException;
import com.lguplus.fleta.exception.latest.DeleteNotFoundException;
import com.lguplus.fleta.service.latest.LatestDomainService;
import com.lguplus.fleta.util.JunitTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.BadSqlGrammarException;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LatestDomainServiceTest {



    @Mock
    LatestMapper latestMapper;

    @Mock
    LatestRepository latestRepository;
    @InjectMocks
    LatestDomainService latestDomainService;


    @BeforeEach
    void setUp() {
        latestDomainService = new LatestDomainService(latestMapper, latestRepository);
        JunitTestUtils.setValue(latestDomainService, "maxCnt", 5);

    }

    //####################### Start 알림등록 ######################

    @Test
    @DisplayName("LatestRepositoryTest.insertLatest 정상적으로 리스트 데이터를 수신하는지 확인")
    void insertLatest() {
        given(latestRepository.insertLatest(any())).willReturn(1);
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021")
                .regId("500023630832")
                .catName("놀라운 대회 스타킹")
                .rDate("")
                .categoryGb("-")
                .build();

        int resultCnt = latestDomainService.insertLatest(latestRequestDto);
        assertThat(resultCnt==0); // mock 의 결과 size 와 메소드 실행 결과 사이즈가 같은지 확인

        log.info("LatestRepositoryTest.insertLatest End");
    }

    @Test
    @DisplayName("LatestRepositoryTest.insertLatest.insertLatest_DatabaseException 에러 확인")
    public void insertLatest_DatabaseException() {
        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021")
                .regId("500023630832")
                .catName("놀라운 대회 스타킹")
                .rDate("")
                .categoryGb("-")
                .build();
        SQLException sqlException = new SQLException();
        given(latestRepository.insertLatest(any())).willThrow(new BadSqlGrammarException("task","sql", sqlException));
        Exception thrown = assertThrows(DatabaseException.class, () -> {
            latestDomainService.insertLatest(latestRequestDto);
        });
        assertEquals(thrown instanceof DatabaseException, true);
        log.info("LatestRepositoryTest.insertLatest_DatabaseException End");
    }


    @Test
    @DisplayName("LatestRepositoryTest.insertLatest.insertLatest_JpaSocketException 에러 확인")
    public void insertLatest_RuntimeException() {
        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021")
                .regId("500023630832")
                .catName("놀라운 대회 스타킹")
                .rDate("")
                .categoryGb("-")
                .build();
        given(latestRepository.insertLatest(any())).willThrow(new RuntimeException());
        Exception thrown = assertThrows(RuntimeException.class, () -> {
            latestDomainService.insertLatest(latestRequestDto);
        });
        assertEquals(thrown instanceof RuntimeException, true);
        log.info("LatestRepositoryTest.insertLatest_RuntimeException End");
    }

    //####################### End 알림등록 ######################


    //####################### Start 알림조회 ######################
    @Test
    @DisplayName("LatestRepositoryTest.getLatestList 정상적으로 리스트 데이터를 수신하는지 확인")
    void getLatestList() {
        LatestEntity rs1 = LatestEntity.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021")
                .regId("500023630832")
                .catName("놀라운 대회 스타킹")
                .rDate("2014-11-09 13:18:14.000")
                .categoryGb("")
                .build();
        List<LatestEntity> list = List.of(rs1);

        // Mock Method
        given(latestRepository.getLatestList(any())).willReturn(list);

        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021").build();

        List<LatestDto> responseList = latestDomainService.getLatestList(latestRequestDto);
        assertThat(responseList.size()==1); // mock 의 결과 size 와 메소드 실행 결과 사이즈가 같은지 확인

        log.info("LatestRepositoryTest.getLatestList End");
    }
    //####################### End 알림조회 ######################


    //####################### Start 알림체크리스트조회 ######################
    @Test
    @DisplayName("LatestRepositoryTest.getLatestCheckList0 정상적으로 체크리스트 데이터를 수신하는지 확인")
    void getLatestCheckList0() {
        // Mock Method
        LatestCheckDto checkDto = LatestCheckDto.builder().build();

        LatestCheckEntity rs1 = LatestCheckEntity.builder().build();
        List<LatestCheckEntity> list = List.of();
        given(latestRepository.getLatestCheckList(any())).willReturn(list);

        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021").build();
        LatestCheckDto responseList = latestDomainService.getLatestCheckList(latestRequestDto);
        assertThat(responseList.getCode().equals(LatestCheckDto.SUCCESS_CODE)); // mock 의 결과 size 와 메소드 실행 결과 사이즈가 같은지 확인

        log.info("LatestRepositoryTest.getLatestCheckList0 End");
    }

    @Test
    @DisplayName("LatestRepositoryTest.getLatestCheckList_DuplicateKeyException 중복체크 조건이 잘 실행되는지 확인")
    void getLatestCheckList_DuplicateKeyException() {
        // --------- Mock Method 강제 에러 연출 ---------
        LatestCheckDto checkDto = LatestCheckDto.builder().build();

        LatestCheckEntity rs1 = LatestCheckEntity.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021")
                .build();

        LatestCheckEntity rs2 = LatestCheckEntity.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021")
                .build();

        List<LatestCheckEntity> list = List.of(rs1, rs2);
        given(latestRepository.getLatestCheckList(any())).willReturn(list);

        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021").build();
        Exception thrown = assertThrows(DuplicateKeyException.class, () -> {
            LatestCheckDto responseDto = latestDomainService.getLatestCheckList(latestRequestDto);
        });

        assertEquals(thrown instanceof DuplicateKeyException, true);
        log.info("LatestRepositoryTest.getLatestCheckList_DuplicateKeyException End");
    }

    @Test
    @DisplayName("LatestRepositoryTest.getLatestCheckList_ExceedMaxRequestException 리스트 최대등록개수 체크가 동작하는지 확인")
    void getLatestCheckList_ExceedMaxRequestException() {
        // --------- Mock Method 강제 에러 연출 ---------
        /*
        [중복조건] SELECT CAT_ID FROM PT_UX_LATEST WHERE SA_ID=#saID# AND MAC=#mac# AND CTN=#ctn#
        */
        LatestCheckDto checkDto = LatestCheckDto.builder().build();

        LatestCheckEntity rs1 = LatestCheckEntity.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021")
                .build();

        LatestCheckEntity rs2 = LatestCheckEntity.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3022")
                .build();

        LatestCheckEntity rs3 = LatestCheckEntity.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3023")
                .build();

        LatestCheckEntity rs4 = LatestCheckEntity.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3024")
                .build();


        LatestCheckEntity rs5 = LatestCheckEntity.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3025")
                .build();

        LatestCheckEntity rs6 = LatestCheckEntity.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3026")
                .build();


        List<LatestCheckEntity> list = List.of(rs1, rs2, rs3, rs4, rs5, rs6);

        given(latestRepository.getLatestCheckList(any())).willReturn(list);

        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .build();
        Exception thrown = assertThrows(ExceedMaxRequestException.class, () -> {
            LatestCheckDto responseDto = latestDomainService.getLatestCheckList(latestRequestDto);
        });

        assertEquals(thrown instanceof ExceedMaxRequestException, true);
        log.info("LatestRepositoryTest.getLatestCheckList_ExceedMaxRequestException End");
    }


    //####################### End 알림체크리스트조회 ######################


    //####################### Start 알림삭제 ######################


    //####################### End 알림삭제 ######################



}