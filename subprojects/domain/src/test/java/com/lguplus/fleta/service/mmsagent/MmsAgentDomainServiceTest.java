package com.lguplus.fleta.service.mmsagent;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.client.MmsAgentDomainClient;
import com.lguplus.fleta.config.MmsAgentConfig;
import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.exception.NoResultException;
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

import java.lang.NumberFormatException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    CallSettingDomainClient apiClient;

    @Mock
    private static MmsAgentDomainClient mmsSoap;

    private static MmsAgentConfig mmsAgentConfig;
    private static Map<String, Object> mmsConfig;//yml파일/mms
    private static Map<String, Object> settingConfig;//yml파일/mms/setting

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

        settingConfig = new HashMap<>();
        settingConfig.put("request_method","GET");
        settingConfig.put("timeout", 5000);
        settingConfig.put("url", "http://hdtv.suxm.uplus.co.kr/hdtv/comm/setting?sa_id=mms&stb_mac=mms&code_id=&svc_type=E");
        settingConfig.put("rest_url", "http://hdtv.suxm.uplus.co.kr");
        settingConfig.put("rest_path", "/hdtv/comm/setting");
        settingConfig.put("rest_sa_id", "mms");
        settingConfig.put("rest_stb_mac","mms");
        settingConfig.put("rest_code_id","");
        settingConfig.put("rest_svc_type","E");

        mmsConfig.put("setting", settingConfig);
        mmsAgentConfig.setMms(mmsConfig);

        mmsAgentDomainService = new MmsAgentDomainService(apiClient, mmsAgentConfig, mmsSoap);
        log.info(mmsAgentDomainService.toString());
    }


    @Test
    @DisplayName("callSettingApi recordset null")
    void sendMmsCode_callSettingApi_recordset_null() {
        List<CallSettingDto> rs = new ArrayList<>();
        CallSettingResultDto result = CallSettingResultDto.builder()
                .flag("0000")
                .message("성공")
                .totalCount(0)
                .memberGroup("")
                .recordset(null)
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

        log.info("End callSettingApi recordset null");
    }

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

    @Test
    @DisplayName("MMS Success case")
    void sendMmsCode_success() {
        CallSettingDto dto = CallSettingDto.builder()
                .codeId("M011")
                .codeName("U+아이들나라는 네이버 예약과 함께 매 주 아이들과 함께 하기 좋은 체험 장소를 소개합니다.")
                .build();
        List<CallSettingDto> rs = new ArrayList<>();
        rs.add(dto);
        CallSettingResultDto result = CallSettingResultDto.builder()
                .flag("0000")
                .message("성공")
                .totalCount(rs.size())
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
                .mmsCd(mmsCd)
                .ctn(ctn)
                .replacement(replacement)
                .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();

        String statusMessage = "1000";
        given( mmsSoap.sendMMS(anyMap(), any())).willReturn(statusMessage);

        SuccessResponseDto responseDto = mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        Assertions.assertTrue("0000".equals(responseDto.getFlag()));
        log.info("End MMS Success case");
    }

    @Test
    @DisplayName("returnMmsCodeError")
    void returnMmsCodeError() {
        CallSettingDto dto = CallSettingDto.builder()
                .codeId("M011")
                .codeName("U+아이들나라는 네이버 예약과 함께 매 주 아이들과 함께 하기 좋은 체험 장소를 소개합니다.")
                .build();
        List<CallSettingDto> rs = new ArrayList<>();
        rs.add(dto);
        CallSettingResultDto result = CallSettingResultDto.builder()
                .flag("0000")
                .message("성공")
                .totalCount(rs.size())
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
        assertEquals(thrown instanceof NoResultException, true);
        log.info("returnMmsCodeError NoResultException 0001 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1500");
        thrown = assertThrows(SystemErrorException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof SystemErrorException, true);
        log.info("returnMmsCodeError SystemErrorException 1500 End");



        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1501");
        thrown = assertThrows(MsgTypeErrorException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof MsgTypeErrorException, true);
        log.info("returnMmsCodeError MsgTypeErrorException 1501 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1502");
        thrown = assertThrows(PhoneNumberErrorException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof PhoneNumberErrorException, true);
        log.info("returnMmsCodeError PhoneNumberErrorException 1502 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1503");
        thrown = assertThrows(SystemBusyException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof SystemBusyException, true);
        log.info("returnMmsCodeError SystemBusyException 1503 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1504");
        thrown = assertThrows(NotSendTimeException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof NotSendTimeException, true);
        log.info("returnMmsCodeError NotSendTimeException 1504 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1505");
        thrown = assertThrows(BlackListException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof BlackListException, true);
        log.info("returnMmsCodeError BlackListException 1505 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("1506");
        thrown = assertThrows(NotFoundMsgException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof NotFoundMsgException, true);
        log.info("returnMmsCodeError NotFoundMsgException 1506 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("5000");
        thrown = assertThrows(ParameterMissingException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof ParameterMissingException, true);
        log.info("returnMmsCodeError ParameterMissingException 5000 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("5001");
        thrown = assertThrows(NumberFormatException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof NumberFormatException, true);
        log.info("returnMmsCodeError NumberFormatException 5001 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("5101");
        thrown = assertThrows(MessageSocketException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof MessageSocketException, true);
        log.info("returnMmsCodeError MessageSocketException 5101 End");



        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("5200");
        thrown = assertThrows(ServerSettingInfoException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof ServerSettingInfoException, true);
        log.info("returnMmsCodeError ServerSettingInfoException 5200 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("5400");
        thrown = assertThrows(NoHttpsException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof NoHttpsException, true);
        log.info("returnMmsCodeError NoHttpsException 5400 End");



        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("8000");
        thrown = assertThrows(DuplicateKeyException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof DuplicateKeyException, true);
        log.info("returnMmsCodeError DuplicateKeyException 8000 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("8999");
        thrown = assertThrows(DatabaseException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof DatabaseException, true);
        log.info("returnMmsCodeError DatabaseException 8999 End");

        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("9998");
        thrown = assertThrows(MmsServiceException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof MmsServiceException, true);
        log.info("returnService MmsServiceException 9998 End");


        given( mmsSoap.sendMMS(anyMap(), any())).willReturn("9999");
        thrown = assertThrows(RuntimeException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof RuntimeException, true);
        log.info("returnService RuntimeException 9999 End");
    }

}