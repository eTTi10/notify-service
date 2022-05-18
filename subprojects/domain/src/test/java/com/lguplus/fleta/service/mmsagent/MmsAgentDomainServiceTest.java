package com.lguplus.fleta.service.mmsagent;

import com.lguplus.fleta.client.MmsAgentClient;
import com.lguplus.fleta.client.SettingDomainClient;
import com.lguplus.fleta.config.MmsAgentConfig;
import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.exception.NoResultException;
import com.lguplus.fleta.exception.mmsagent.NumberFormatException;
import com.lguplus.fleta.exception.mmsagent.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;

@MockitoSettings(strictness = Strictness.LENIENT)
@Slf4j
class MmsAgentDomainServiceTest {

    @InjectMocks
    private MmsAgentDomainService mmsAgentDomainService;

    @Mock
    SettingDomainClient apiClient;

    @Mock
    private static MmsAgentClient mmsSoap;

    private static MmsAgentConfig mmsAgentConfig;
    private static Map<String, Object> mmsConfig;//yml파일/mms

    private static final String saId = "M14070200159";
    private static final String stbMac = "9893.cc1f.e11c";
    private static final String mmsCd = "M011";
    private static final String ctn = "01025851111";
    private static final String replacement = "영희|컴퓨터";
    private static final String svcType = "E";

    @BeforeEach
    void setUp() {
        mmsAgentConfig = new MmsAgentConfig();
        mmsConfig = new HashMap<>();
        mmsConfig.put("X_Kmms_SVCCODE", 1);
        mmsConfig.put("X_Kmms_TextInput", 1);
        mmsConfig.put("X_Kmms_redistribution", "1.0");
        mmsConfig.put("callback", "16447000");
        mmsConfig.put("connecttimeout", 3000);
        mmsConfig.put("debug_mode", "N");
        mmsConfig.put("namespace", "http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-2");
        mmsConfig.put("prefix", "mm7");
        mmsConfig.put("readtimeout", 5000);
        mmsConfig.put("sender", "01057404003");
        mmsConfig.put("server_url", "http://61.101.246.151:888");
        mmsConfig.put("vasid", "MMSTVMOBILE");
        mmsConfig.put("vaspid", "MMSTVMOBILE");
        mmsConfig.put("version", "5.3.0");

        mmsAgentConfig.setMms(mmsConfig);

        mmsAgentDomainService = new MmsAgentDomainService(apiClient, mmsAgentConfig, mmsSoap);
        ReflectionTestUtils.invokeMethod(mmsAgentDomainService, "initialized");
        log.info(mmsAgentDomainService.toString());
    }


    @Test
    @DisplayName("callSettingApi recordset null")
    void sendMmsCode_callSettingApi_recordset_null() {
        CallSettingDto dto = CallSettingDto.builder()
                .code("M011")
                .name("U+아이들나라는 네이버 예약과 함께 매 주 아이들과 함께 하기 좋은 체험 장소를 소개합니다.")
                .build();

        CallSettingResultDto result = CallSettingResultDto.builder()
                .dataType("SINGLE")
                .dataCount(1)
                .data(null)
                .build();

        CallSettingResultMapDto callSettingDto = CallSettingResultMapDto.builder()//결과객체
                .result(result)
                .build();
        given( apiClient.callSettingApi(any())).willReturn(callSettingDto);

        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
                .saId(saId)
                .stbMac(stbMac)
                .mmsCd("M999")
                .ctn(ctn)
                .replacement(replacement)
                .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();

        Exception thrown = assertThrows(NotFoundMsgException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof NotFoundMsgException);

        log.info("End callSettingApi recordset null");
    }
/*
    @Test
    @DisplayName("callSettingApi totalcount0")
    void sendMmsCode_callSettingApi_totalcount0() {
        List<CallSettingDto> rs = new ArrayList<>();
        CallSettingResultDto result = CallSettingResultDto.builder()
                .flag("0000")
                .message("성공")
                .totalCount(0)
                .memberGroup("")
                .recordset(rs)
                .build();
        CallSettingResultMapDto callSettingDto = CallSettingResultMapDto.builder()//결과객체
                .result(result)
                .build();
        given( apiClient.mmsCallSettingApi(any())).willReturn(callSettingDto);

        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
                .saId(saId)
                .stbMac(stbMac)
                .mmsCd("M999")
                .ctn(ctn)
                .replacement(replacement)
                .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();

        Exception thrown = assertThrows(NotFoundMsgException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof NotFoundMsgException, true);

        log.info("End callSettingApi totalcount0");
    }
*/

    @Test
    @DisplayName("MMS Success case")
    void sendMmsCode_success() {
        CallSettingDto dto = CallSettingDto.builder()
                .code("M011")
                .name("U+아이들나라는 네이버 예약과 함께 매 주 아이들과 함께 하기 좋은 체험 장소를 소개합니다.")
                .build();

        CallSettingResultDto result = CallSettingResultDto.builder()
                .dataType("SINGLE")
                .dataCount(1)
                .data(dto)
                .build();

        CallSettingResultMapDto callSettingDto = CallSettingResultMapDto.builder()//결과객체
                .result(result)
                .build();
        given( apiClient.callSettingApi(any())).willReturn(callSettingDto);


        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
                .saId(saId)
                .stbMac(stbMac)
                .mmsCd(mmsCd)
                .ctn(ctn)
                .replacement(replacement)
                .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();

        String statusMessage = "1000";
        given( mmsSoap.sendMMS(anyMap(), any())).willReturn(statusMessage);

        SuccessResponseDto responseDto = mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        Assertions.assertEquals("0000", responseDto.getFlag());
        log.info("End MMS Success case");
    }

    @Test
    @DisplayName("returnMmsCodeError - 0xxx")
    void returnMmsCodeError_0xxx() {
        CallSettingDto dto = CallSettingDto.builder()
                .code("M011")
                .name("U+아이들나라는 네이버 예약과 함께 매 주 아이들과 함께 하기 좋은 체험 장소를 소개합니다.")
                .build();

        CallSettingResultDto result = CallSettingResultDto.builder()
                .dataType("SINGLE")
                .dataCount(1)
                .data(dto)
                .build();

        CallSettingResultMapDto callSettingDto = CallSettingResultMapDto.builder()//결과객체
                .result(result)
                .build();
        given( apiClient.callSettingApi(any())).willReturn(callSettingDto);



        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
                .saId(saId)
                .stbMac(stbMac)
                .mmsCd(mmsCd)
                .ctn(ctn)
                .replacement(replacement)
                .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();


        Exception thrown;
        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("0001");
        thrown = assertThrows(NoResultException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof NoResultException);
        log.info("returnMmsCodeError NoResultException 0001 End");
    }

    @Test
    @DisplayName("returnMmsCodeError - 1xxx")
    void returnMmsCodeError_1xxx() {
        CallSettingDto dto = CallSettingDto.builder()
                .code("M011")
                .name("U+아이들나라는 네이버 예약과 함께 매 주 아이들과 함께 하기 좋은 체험 장소를 소개합니다.")
                .build();

        CallSettingResultDto result = CallSettingResultDto.builder()
                .dataType("SINGLE")
                .dataCount(1)
                .data(dto)
                .build();

        CallSettingResultMapDto callSettingDto = CallSettingResultMapDto.builder()//결과객체
                .result(result)
                .build();
        given( apiClient.callSettingApi(any())).willReturn(callSettingDto);



        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
                .saId(saId)
                .stbMac(stbMac)
                .mmsCd(mmsCd)
                .ctn(ctn)
                .replacement(replacement)
                .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();


        Exception thrown;
        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1500");
        thrown = assertThrows(SystemErrorException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof SystemErrorException);
        log.info("returnMmsCodeError SystemErrorException 1500 End");



        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1501");
        thrown = assertThrows(MsgTypeErrorException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof MsgTypeErrorException);
        log.info("returnMmsCodeError MsgTypeErrorException 1501 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1502");
        thrown = assertThrows(PhoneNumberErrorException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof PhoneNumberErrorException);
        log.info("returnMmsCodeError PhoneNumberErrorException 1502 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1503");
        thrown = assertThrows(SystemBusyException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof SystemBusyException);
        log.info("returnMmsCodeError SystemBusyException 1503 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1504");
        thrown = assertThrows(NotSendTimeException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof NotSendTimeException);
        log.info("returnMmsCodeError NotSendTimeException 1504 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1505");
        thrown = assertThrows(BlackListException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof BlackListException);
        log.info("returnMmsCodeError BlackListException 1505 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1506");
        thrown = assertThrows(NotFoundMsgException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof NotFoundMsgException);
        log.info("returnMmsCodeError NotFoundMsgException 1506 End");
    }

    @Test
    @DisplayName("returnMmsCodeError - 5xxx")
    void returnMmsCodeError_5xxx() {
        CallSettingDto dto = CallSettingDto.builder()
                .code("M011")
                .name("U+아이들나라는 네이버 예약과 함께 매 주 아이들과 함께 하기 좋은 체험 장소를 소개합니다.")
                .build();

        CallSettingResultDto result = CallSettingResultDto.builder()
                .dataType("SINGLE")
                .dataCount(1)
                .data(dto)
                .build();

        CallSettingResultMapDto callSettingDto = CallSettingResultMapDto.builder()//결과객체
                .result(result)
                .build();
        given( apiClient.callSettingApi(any())).willReturn(callSettingDto);



        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
                .saId(saId)
                .stbMac(stbMac)
                .mmsCd(mmsCd)
                .ctn(ctn)
                .replacement(replacement)
                .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();


        Exception thrown;
        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("5000");
        thrown = assertThrows(ParameterMissingException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof ParameterMissingException);
        log.info("returnMmsCodeError ParameterMissingException 5000 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("5001");
        thrown = assertThrows(NumberFormatException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof NumberFormatException);
        log.info("returnMmsCodeError NumberFormatException 5001 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("5101");
        thrown = assertThrows(MessageSocketException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof MessageSocketException);
        log.info("returnMmsCodeError MessageSocketException 5101 End");



        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("5200");
        thrown = assertThrows(ServerSettingInfoException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof ServerSettingInfoException);
        log.info("returnMmsCodeError ServerSettingInfoException 5200 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("5400");
        thrown = assertThrows(NoHttpsException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof NoHttpsException);
        log.info("returnMmsCodeError NoHttpsException 5400 End");
    }

    @Test
    @DisplayName("returnMmsCodeError - 8xxx")
    void returnMmsCodeError_8xxx() {
        CallSettingDto dto = CallSettingDto.builder()
                .code("M011")
                .name("U+아이들나라는 네이버 예약과 함께 매 주 아이들과 함께 하기 좋은 체험 장소를 소개합니다.")
                .build();

        CallSettingResultDto result = CallSettingResultDto.builder()
                .dataType("SINGLE")
                .dataCount(1)
                .data(dto)
                .build();

        CallSettingResultMapDto callSettingDto = CallSettingResultMapDto.builder()//결과객체
                .result(result)
                .build();
        given( apiClient.callSettingApi(any())).willReturn(callSettingDto);



        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
                .saId(saId)
                .stbMac(stbMac)
                .mmsCd(mmsCd)
                .ctn(ctn)
                .replacement(replacement)
                .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();


        Exception thrown;
        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("8000");
        thrown = assertThrows(DuplicateKeyException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof DuplicateKeyException);
        log.info("returnMmsCodeError DuplicateKeyException 8000 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("8999");
        thrown = assertThrows(DatabaseException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof DatabaseException);
        log.info("returnMmsCodeError DatabaseException 8999 End");
    }

    @Test
    @DisplayName("returnMmsCodeError - 9xxx")
    void returnMmsCodeError_9xxx() {
        CallSettingDto dto = CallSettingDto.builder()
                .code("M011")
                .name("U+아이들나라는 네이버 예약과 함께 매 주 아이들과 함께 하기 좋은 체험 장소를 소개합니다.")
                .build();

        CallSettingResultDto result = CallSettingResultDto.builder()
                .dataType("SINGLE")
                .dataCount(1)
                .data(dto)
                .build();

        CallSettingResultMapDto callSettingDto = CallSettingResultMapDto.builder()//결과객체
                .result(result)
                .build();
        given( apiClient.callSettingApi(any())).willReturn(callSettingDto);



        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
                .saId(saId)
                .stbMac(stbMac)
                .mmsCd(mmsCd)
                .ctn(ctn)
                .replacement(replacement)
                .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();


        Exception thrown;
        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("9998");
        thrown = assertThrows(MmsServiceException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof MmsServiceException);
        log.info("returnService MmsServiceException 9998 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("9999");
        thrown = assertThrows(MmsRuntimeException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(true, thrown instanceof MmsRuntimeException);
        log.info("returnService RuntimeException 9999 End");
    }

}