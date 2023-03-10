package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestCheckDto;
import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.Latest;
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

    //####################### Start ???????????? ######################

    @Test
    @DisplayName("????????? ????????????")
    void insertLatest() {
        //given(latestRepository.insertLatest(any())).willReturn(GET_UUID);
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId(GET_UUID)
            .mac(MAC)
            .ctn(CTN)
            .catId(CAT_ID)
            .regId(REG_ID)
            .catName("????????? ?????? ?????????")
            .categoryGb(CATEGORY_GB)
            .build();

        assertDoesNotThrow(() -> latestDomainService.insertLatest(latestRequestDto));
        //assertEquals(resultCode, GET_UUID); // mock ??? ?????? size ??? ????????? ?????? ?????? ???????????? ????????? ??????

        log.info("????????? ????????????");
    }

    @Test
    @DisplayName("????????? DatabaseException ?????? ??????")
    void insertLatest_DatabaseException() {
        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId(GET_UUID)
            .mac(MAC)
            .ctn(CTN)
            .catId(CAT_ID)
            .regId(REG_ID)
            .catName("????????? ?????? ?????????")
            .categoryGb(CATEGORY_GB)
            .build();
        SQLException sqlException = new SQLException();
        given(latestRepository.insertLatest(any())).willThrow(new BadSqlGrammarException("task", "sql", sqlException));
        Exception thrown = assertThrows(DatabaseException.class, () -> {
            latestDomainService.insertLatest(latestRequestDto);
        });
        assertEquals(true, thrown instanceof DatabaseException);
        log.info("????????? DatabaseException ?????? ??????");
    }


    @Test
    @DisplayName("????????? JpaSocketException ?????? ??????")
    void insertLatest_RuntimeException() {
        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId(GET_UUID)
            .mac(MAC)
            .ctn(CTN)
            .catId(CAT_ID)
            .regId(REG_ID)
            .catName("????????? ?????? ?????????")
            .categoryGb(CATEGORY_GB)
            .build();
        given(latestRepository.insertLatest(any())).willThrow(new ExtRuntimeException());
        Exception thrown = assertThrows(ExtRuntimeException.class, () -> {
            latestDomainService.insertLatest(latestRequestDto);
        });
        assertEquals(true, thrown instanceof RuntimeException);
        log.info("????????? JpaSocketException ?????? ??????");
    }

    //####################### End ???????????? ######################


    //####################### Start ???????????? ######################
    @Test
    @DisplayName("?????? ??????????????? ????????? ???????????? ??????????????? ??????")
    void getLatestList() {
        Latest rs1 = Latest.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3021")
            .regId("500023630832")
            .catName("????????? ?????? ?????????")
            .rDate(new Date())
            .categoryGb("")
            .build();
        List<Latest> list = List.of(rs1);

        // Mock Method
        given(latestRepository.getLatestList(any())).willReturn(list);

        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3021").build();

        List<LatestDto> responseList = latestDomainService.getLatestList(latestRequestDto);
        assertThat(responseList.size()).isEqualTo(1); // mock ??? ?????? size ??? ????????? ?????? ?????? ???????????? ????????? ??????

        log.info("?????? ??????????????? ????????? ???????????? ??????????????? ??????");
    }
    //####################### End ???????????? ######################


    //####################### Start ??????????????????????????? ######################
    @Test
    @DisplayName("??????????????? ??????????????? ???????????? ??????????????? ??????")
    void getLatestCheckList0() {
        // Mock Method
        LatestCheckDto checkDto = LatestCheckDto.builder().build();

        Latest rs1 = Latest.builder().build();
        List<Latest> list = List.of();
        given(latestRepository.getLatestCheckList(any())).willReturn(list);

        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3021").build();
        LatestCheckDto responseList = latestDomainService.getLatestCheckList(latestRequestDto);
        assertThat(responseList.getCode()).isEqualTo(LatestCheckDto.SUCCESS_CODE); // mock ??? ?????? size ??? ????????? ?????? ?????? ???????????? ????????? ??????

        log.info("??????????????? ??????????????? ???????????? ??????????????? ??????");
    }

    @Test
    @DisplayName("???????????? ????????? ??? ??????????????? ??????")
    void getLatestCheckList_DuplicateKeyException() {
        // --------- Mock Method ?????? ?????? ?????? ---------
        LatestCheckDto checkDto = LatestCheckDto.builder().build();

        Latest rs1 = Latest.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3021")
            .build();

        Latest rs2 = Latest.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3021")
            .build();

        List<Latest> list = List.of(rs1, rs2);
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
        log.info("???????????? ????????? ??? ??????????????? ??????");
    }

    @Test
    @DisplayName("????????? ?????????????????? ????????? ??????????????? ??????")
    void getLatestCheckList_ExceedMaxRequestException() {
        // --------- Mock Method ?????? ?????? ?????? ---------
        /*
        [????????????] SELECT CAT_ID FROM PT_UX_LATEST WHERE SA_ID=#saID# AND MAC=#mac# AND CTN=#ctn#
        */
        LatestCheckDto checkDto = LatestCheckDto.builder().build();

        Latest rs1 = Latest.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3021")
            .build();

        Latest rs2 = Latest.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3022")
            .build();

        Latest rs3 = Latest.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3023")
            .build();

        Latest rs4 = Latest.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3024")
            .build();

        Latest rs5 = Latest.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3025")
            .build();

        Latest rs6 = Latest.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .catId("T3026")
            .build();

        List<Latest> list = List.of(rs1, rs2, rs3, rs4, rs5, rs6);

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
        log.info("????????? ?????????????????? ????????? ??????????????? ??????");
    }

    //####################### End ??????????????????????????? ######################


    //####################### Start ???????????? ######################
    @Test
    @DisplayName("??????????????? ????????? ???????????? ??????????????? ??????")
    void deleteLatest() {
        given(latestRepository.deleteLatest(any())).willReturn(1);

        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId("500023630832")
            .mac("001c.6284.30a4")
            .ctn("01080808526")
            .catId("M0241").build();

        int deleteCnt = latestDomainService.deleteLatest(latestRequestDto);
        Assertions.assertEquals(1, deleteCnt);

        log.info("??????????????? ????????? ???????????? ??????????????? ??????");
    }


    @Test
    @DisplayName("LatestServiceTest.deleteLatestDeleteNotFoundException ?????? ?????? ????????? ??????")
    void deleteLatestDeleteNotFoundException() {
        given(latestRepository.deleteLatest(any())).willReturn(0);

        Exception thrown = assertThrows(DeleteNotFoundException.class, () -> {
            latestDomainService.deleteLatest(null);
        });
        assertEquals(true, thrown instanceof DeleteNotFoundException);

        log.info("LatestServiceTest.deleteLatestDeleteNotFoundException End");
    }

    //####################### End ???????????? ######################


}