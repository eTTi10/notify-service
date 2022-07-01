package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestCheckDto;
import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.data.mapper.LatestMapper;
import com.lguplus.fleta.exception.ExceedMaxRequestException;
import com.lguplus.fleta.exception.ExtRuntimeException;
import com.lguplus.fleta.exception.database.DataAlreadyExistsException;
import com.lguplus.fleta.exception.database.DatabaseException;
import com.lguplus.fleta.exception.latest.DeleteNotFoundException;
import com.lguplus.fleta.repository.latest.LatestRepository;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.BadSqlGrammarException;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LatestDomainServiceTest {

    private static final String MAC = "JUNIT_TEST_MAC";
    private static final String CTN = "01012341234";
    private static final String CAT_ID = "T0070";
    private static final String REG_ID = "000011112222";
    private static final String CATEGORY_GB = "JUN";
    private static final String GET_UUID = getUUID();
    @Mock
    LatestMapper latestMapper;

    @Mock
    LatestRepository latestRepository;
    @InjectMocks
    LatestDomainService latestDomainService;

    public static String getUUID() {
        Date now = new Date();
        Calendar currentDate = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyMMddHHmmss"); //yyMMddHHmmss
        String returnValue = df.format(currentDate.getTime());
        log.info("GET_UUID : " + returnValue);
        return (returnValue);
    }

    //    @BeforeEach
    //    void setUp() {
    //        GET_UUID = getUUID();
    //
    //        latestDomainService = new LatestDomainService(latestMapper, latestRepository);
    //        ReflectionTestUtils.setField(latestDomainService, "maxCnt", 5);
    //
    //    }

    //####################### Start 알림등록 ######################

    @Test
    @DisplayName("인서트 정상실행")
    void insertLatest() {
        //given(latestRepository.insertLatest(any())).willReturn(GET_UUID);
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId(GET_UUID)
            .mac(MAC)
            .ctn(CTN)
            .catId(CAT_ID)
            .regId(REG_ID)
            .catName("놀라운 대회 스타킹")
            .categoryGb(CATEGORY_GB)
            .build();

        assertDoesNotThrow(() -> latestDomainService.insertLatest(latestRequestDto));
        //assertEquals(resultCode, GET_UUID); // mock 의 결과 size 와 메소드 실행 결과 사이즈가 같은지 확인

        log.info("인서트 정상실행");
    }

    @Test
    @DisplayName("인서트 DatabaseException 에러 확인")
    void insertLatest_DatabaseException() {
        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId(GET_UUID)
            .mac(MAC)
            .ctn(CTN)
            .catId(CAT_ID)
            .regId(REG_ID)
            .catName("놀라운 대회 스타킹")
            .categoryGb(CATEGORY_GB)
            .build();
        SQLException sqlException = new SQLException();
        given(latestRepository.insertLatest(any())).willThrow(new BadSqlGrammarException("task", "sql", sqlException));
        Exception thrown = assertThrows(DatabaseException.class, () -> {
            latestDomainService.insertLatest(latestRequestDto);
        });
        assertEquals(true, thrown instanceof DatabaseException);
        log.info("인서트 DatabaseException 에러 확인");
    }


    @Test
    @DisplayName("인서트 JpaSocketException 에러 확인")
    void insertLatest_RuntimeException() {
        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId(GET_UUID)
            .mac(MAC)
            .ctn(CTN)
            .catId(CAT_ID)
            .regId(REG_ID)
            .catName("놀라운 대회 스타킹")
            .categoryGb(CATEGORY_GB)
            .build();
        given(latestRepository.insertLatest(any())).willThrow(new ExtRuntimeException());
        Exception thrown = assertThrows(ExtRuntimeException.class, () -> {
            latestDomainService.insertLatest(latestRequestDto);
        });
        assertEquals(true, thrown instanceof RuntimeException);
        log.info("인서트 JpaSocketException 에러 확인");
    }

    //####################### End 알림등록 ######################


    //####################### Start 알림조회 ######################
    @Test
    @DisplayName("조회 정상적으로 리스트 데이터를 수신하는지 확인")
    void getLatestList() {
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
        assertThat(responseList.size()).isEqualTo(1); // mock 의 결과 size 와 메소드 실행 결과 사이즈가 같은지 확인

        log.info("조회 정상적으로 리스트 데이터를 수신하는지 확인");
    }
    //####################### End 알림조회 ######################


    //####################### Start 알림체크리스트조회 ######################
    @Test
    @DisplayName("정상적으로 체크리스트 데이터를 수신하는지 확인")
    void getLatestCheckList0() {
        // Mock Method
        LatestCheckDto checkDto = LatestCheckDto.builder().build();

        LatestEntity rs1 = LatestEntity.builder().build();
        List<LatestEntity> list = List.of();
        given(latestRepository.getLatestCheckList(any())).willReturn(list);

        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3021").build();
        LatestCheckDto responseList = latestDomainService.getLatestCheckList(latestRequestDto);
        assertThat(responseList.getCode()).isEqualTo(LatestCheckDto.SUCCESS_CODE); // mock 의 결과 size 와 메소드 실행 결과 사이즈가 같은지 확인

        log.info("정상적으로 체크리스트 데이터를 수신하는지 확인");
    }

    @Test
    @DisplayName("중복체크 조건이 잘 실행되는지 확인")
    void getLatestCheckList_DuplicateKeyException() {
        // --------- Mock Method 강제 에러 연출 ---------
        LatestCheckDto checkDto = LatestCheckDto.builder().build();

        LatestEntity rs1 = LatestEntity.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3021")
            .build();

        LatestEntity rs2 = LatestEntity.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3021")
            .build();

        List<LatestEntity> list = List.of(rs1, rs2);
        given(latestRepository.getLatestCheckList(any())).willReturn(list);

        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3021").build();
        Exception thrown = assertThrows(DataAlreadyExistsException.class, () -> {
            LatestCheckDto responseDto = latestDomainService.getLatestCheckList(latestRequestDto);
        });

        assertEquals(true, thrown instanceof DataAlreadyExistsException);
        log.info("중복체크 조건이 잘 실행되는지 확인");
    }

    @Test
    @DisplayName("리스트 최대등록개수 체크가 동작하는지 확인")
    void getLatestCheckList_ExceedMaxRequestException() {
        // --------- Mock Method 강제 에러 연출 ---------
        /*
        [중복조건] SELECT CAT_ID FROM PT_UX_LATEST WHERE SA_ID=#saID# AND MAC=#mac# AND CTN=#ctn#
        */
        LatestCheckDto checkDto = LatestCheckDto.builder().build();

        LatestEntity rs1 = LatestEntity.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3021")
            .build();

        LatestEntity rs2 = LatestEntity.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3022")
            .build();

        LatestEntity rs3 = LatestEntity.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3023")
            .build();

        LatestEntity rs4 = LatestEntity.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3024")
            .build();

        LatestEntity rs5 = LatestEntity.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3025")
            .build();

        LatestEntity rs6 = LatestEntity.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3026")
            .build();

        List<LatestEntity> list = List.of(rs1, rs2, rs3, rs4, rs5, rs6);

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

        assertEquals(true, thrown instanceof ExceedMaxRequestException);
        log.info("리스트 최대등록개수 체크가 동작하는지 확인");
    }

    //####################### End 알림체크리스트조회 ######################


    //####################### Start 알림삭제 ######################
    @Test
    @DisplayName("정상적으로 리스트 데이터를 삭제하는지 확인")
    void deleteLatest() {
        given(latestRepository.deleteLatest(any())).willReturn(1);

        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId("500023630832")
            .mac("001c.6284.30a4")
            .ctn("01080808526")
            .catId("M0241").build();

        int deleteCnt = latestDomainService.deleteLatest(latestRequestDto);
        Assertions.assertEquals(1, deleteCnt);

        log.info("정상적으로 리스트 데이터를 삭제하는지 확인");
    }


    @Test
    @DisplayName("LatestServiceTest.deleteLatestDeleteNotFoundException 예외 처리 되는지 확인")
    void deleteLatestDeleteNotFoundException() {
        given(latestRepository.deleteLatest(any())).willReturn(0);

        Exception thrown = assertThrows(DeleteNotFoundException.class, () -> {
            latestDomainService.deleteLatest(null);
        });
        assertEquals(true, thrown instanceof DeleteNotFoundException);

        log.info("LatestServiceTest.deleteLatestDeleteNotFoundException End");
    }

    //####################### End 알림삭제 ######################


}