package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.Latest;
import com.lguplus.fleta.domain.repository.LatestRepositoryImpl;
import com.lguplus.fleta.repository.latest.LatestRepository;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class LatestJpaRepositoryTest {

    private static final String MAC = "JUNIT_TEST_MAC";
    private static final String CTN = "01012341234";
    private static final String CAT_ID = "T0070";
    private static final String REG_ID = "000011112222";
    private static final String CATEGORY_GB = "JUN";
    private static String GET_UUID;
    @Mock
    LatestRepository latestRepository;
    @Mock
    LatestJpaRepository latestJpaRepository;
    @InjectMocks
    LatestRepositoryImpl latestRepositoryImpl;
    @Mock
    private EntityManager em;

    public static String getUUID() {
        Date now = new Date();
        Calendar currentDate = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyMMddHHmmss"); //yyMMddHHmmss
        String returnValue = df.format(currentDate.getTime());
        log.info("GET_UUID : " + returnValue);
        return (returnValue);
    }

    @BeforeEach
    void setUp() {
        GET_UUID = getUUID();
    }

    //####################### Start 알람 리스트등록 ######################
    @Test
    @DisplayName("LatestJpaJpaRepositoryTest.getInsertLatest 정상적으로 리스트 데이터를 등록하는지 확인")
    void getInsertLatest() {
        log.info(GET_UUID);
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
        assertDoesNotThrow(() -> latestRepositoryImpl.insertLatest(latestRequestDto));

        log.info("LatestJpaJpaRepositoryTest.getLatestCheckList End");
    }
    //####################### End 알람 리스트등록 ######################


    //####################### Start 알림조회 ######################
    @Test
    @DisplayName("LatestJpaJpaRepositoryTest.getLatestList 정상적으로 리스트 데이터를 수신하는지 확인")
    void getLatestList() {
        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId(GET_UUID)
            .mac(MAC)
            .ctn(CTN)
            .catId(CAT_ID)
            .build();
        List<Latest> responseList = latestRepositoryImpl.getLatestList(latestRequestDto);
        // 결과값은 0건 또는 1건
        assertThat(responseList.size()).isLessThan(2).isNotNegative();

        log.info("LatestJpaJpaRepositoryTest.getLatestList End");
    }

    @Test
    @DisplayName("LatestJpaJpaRepositoryTest.getLatestList 정상적으로 리스트 데이터를 수신하는지 확인")
    void getLatestList_catIdNull() {

        LatestRequestDto latestRequestDto2 = LatestRequestDto.builder()
            .saId(GET_UUID)
            .mac(MAC)
            .ctn(CTN)
            .catId("")
            .build();
        List<Latest> responseList = latestRepositoryImpl.getLatestList(latestRequestDto2);
        //결과값은 0건이거나 1건 이상이다.
        assertThat(responseList.size()).isNotNegative();

        log.info("LatestJpaJpaRepositoryTest.getLatestList_catIdNull End");
    }
    //####################### End 알림조회 ######################


    //####################### Start 알람 체크리스트 조회 ######################
    @Test
    @DisplayName("LatestJpaJpaRepositoryTest.getLatestCheckList 정상적으로 리스트 데이터를 수신하는지 확인")
    void getLatestCheckList() {
        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId("123456789123")
            .mac("1111.2222.3333")
            .ctn("01011112222")
            .build();
        List<Latest> responseList = latestRepositoryImpl.getLatestCheckList(latestRequestDto);
        // 결과값은 0건 또는 1건
        assertThat(responseList.size()).isLessThan(2).isNotNegative();

        log.info("LatestJpaJpaRepositoryTest.getLatestCheckList End");
    }

    @Test
    @DisplayName("LatestJpaJpaRepositoryTest.getLatestList 정상적으로 리스트 데이터를 수신하는지 확인")
    void getLatestCheckList_catIdNull() {

        LatestRequestDto latestRequestDto2 = LatestRequestDto.builder()
            .saId("500058151453")
            .mac("001c.627e.039c")
            .ctn("01055805424")
            .build();
        List<Latest> responseList = latestRepositoryImpl.getLatestCheckList(latestRequestDto2);
        //결과값은 0건이거나 1건 이상이다.
        assertThat(responseList.size()).isNotNegative();

        log.info("LatestJpaJpaRepositoryTest.getLatestCheckList_catIdNull End");
    }
    //####################### End 알람 체크리스트 조회 ######################


    //####################### Start 알람 리스트삭제 테스트 ######################
    @Test
    @DisplayName("LatestJpaJpaRepositoryTest.deleteLatest 정상적으로 리스트 데이터를 등록하는지 확인")
    void deleteLatest() {
        log.info(GET_UUID);
        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
            .saId(GET_UUID)
            .mac(MAC)
            .ctn(CTN)
            .catId(CAT_ID)
            .build();
        int resultCnt = latestRepositoryImpl.deleteLatest(latestRequestDto);
        // 결과값은 0건 또는 1건
        assertThat(resultCnt).isLessThan(2).isNotNegative();

        log.info("LatestJpaJpaRepositoryTest.deleteLatest End");
    }
    //####################### End 알람 리스트삭제 테스트 ######################
}
