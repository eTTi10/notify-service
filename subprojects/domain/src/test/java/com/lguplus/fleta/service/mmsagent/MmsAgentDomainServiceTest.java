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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.NumberFormatException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Slf4j
class MmsAgentDomainServiceTest {

    @InjectMocks
    private MmsAgentDomainService mmsAgentDomainService;

    @Mock
    CallSettingDomainClient apiClient;

    @Mock
    MmsAgentDomainClient mmsSoap;

    private static MmsAgentConfig mmsAgentConfig;
    private static Map<String, Object> mmsConfig;//yml파일/mms
    private static Map<String, Object> settingConfig;//yml파일/mms/setting

    private static final String saId = "M14070200159";
    private static final String stbMac = "9893.cc1f.e11c";
    private static final String mmsCd = "M011";
    private static final String ctn = "01025851531";
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

/*

    @Test
    @DisplayName("MMS메세지 NotFoundMsgException case 메세지목록이 0건")
    void sendMmsCode_callSettingApi_NotFoundMsgException() {
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
                .mmsCd(mmsCd)
                .ctn(ctn)
                .replacement(replacement)
                .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();
        String statusMessage = "1000";
        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn(statusMessage);

        Exception thrown = assertThrows(NotFoundMsgException.class, () -> {
            mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        });
        assertEquals(thrown instanceof NotFoundMsgException, true);
        log.info("LatestRepositoryTest.sendMmsCode_callSettingApi_NotFoundMsgException End");
    }
*/






    @Test
    @DisplayName("MMS Success case 성공케이스")
    void sendMmsCode_callSettingApi_success() {
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
        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn(statusMessage);


        SuccessResponseDto responseDto = mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        Assertions.assertTrue("1000".equals(responseDto.getFlag()));
        log.info("End MmsAgentDomainServiceTest.sendMmsCode_callSettingApi_success");
    }

    @Test
    @DisplayName("returnService Exception")
    void returnService() {
        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
                .saId(saId)
                .stbMac(stbMac)
                .mmsCd(mmsCd)
                .ctn(ctn)
                .replacement(replacement)
                .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();


        Exception thrown;
        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("0001");
        thrown = assertThrows(NoResultException.class, () -> {
            mmsAgentDomainService.returnService("0001");
        });
        assertEquals(thrown instanceof NoResultException, true);
        log.info("returnService NoResultException 0001 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("1500");
        thrown = assertThrows(SystemErrorException.class, () -> {
            mmsAgentDomainService.returnService("1500");
        });
        assertEquals(thrown instanceof SystemErrorException, true);
        log.info("returnService SystemErrorException 1500 End");



        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("1501");
        thrown = assertThrows(MsgTypeErrorException.class, () -> {
            mmsAgentDomainService.returnService("1501");
        });
        assertEquals(thrown instanceof MsgTypeErrorException, true);
        log.info("returnService MsgTypeErrorException 1501 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("1502");
        thrown = assertThrows(PhoneNumberErrorException.class, () -> {
            mmsAgentDomainService.returnService("1502");
        });
        assertEquals(thrown instanceof PhoneNumberErrorException, true);
        log.info("returnService PhoneNumberErrorException 1502 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("1502");
        thrown = assertThrows(SystemBusyException.class, () -> {
            mmsAgentDomainService.returnService("1503");
        });
        assertEquals(thrown instanceof SystemBusyException, true);
        log.info("returnService SystemBusyException 1503 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("1502");
        thrown = assertThrows(NotSendTimeException.class, () -> {
            mmsAgentDomainService.returnService("1504");
        });
        assertEquals(thrown instanceof NotSendTimeException, true);
        log.info("returnService NotSendTimeException 1504 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("1502");
        thrown = assertThrows(BlackListException.class, () -> {
            mmsAgentDomainService.returnService("1505");
        });
        assertEquals(thrown instanceof BlackListException, true);
        log.info("returnService BlackListException 1505 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("1502");
        thrown = assertThrows(NotFoundMsgException.class, () -> {
            mmsAgentDomainService.returnService("1506");
        });
        assertEquals(thrown instanceof NotFoundMsgException, true);
        log.info("returnService NotFoundMsgException 1506 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("5000");
        thrown = assertThrows(ParameterMissingException.class, () -> {
            mmsAgentDomainService.returnService("5000");
        });
        assertEquals(thrown instanceof ParameterMissingException, true);
        log.info("returnService ParameterMissingException 5000 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("5001");
        thrown = assertThrows(NumberFormatException.class, () -> {
            mmsAgentDomainService.returnService("5001");
        });
        assertEquals(thrown instanceof NumberFormatException, true);
        log.info("returnService NumberFormatException 5001 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("5101");
        thrown = assertThrows(MessageSocketException.class, () -> {
            mmsAgentDomainService.returnService("5101");
        });
        assertEquals(thrown instanceof MessageSocketException, true);
        log.info("returnService MessageSocketException 5101 End");



        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("5200");
        thrown = assertThrows(ServerSettingInfoException.class, () -> {
            mmsAgentDomainService.returnService("5200");
        });
        assertEquals(thrown instanceof ServerSettingInfoException, true);
        log.info("returnService ServerSettingInfoException 5200 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("5400");
        thrown = assertThrows(NoHttpsException.class, () -> {
            mmsAgentDomainService.returnService("5400");
        });
        assertEquals(thrown instanceof NoHttpsException, true);
        log.info("returnService NoHttpsException 5400 End");



        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("8000");
        thrown = assertThrows(DuplicateKeyException.class, () -> {
            mmsAgentDomainService.returnService("8000");
        });
        assertEquals(thrown instanceof DuplicateKeyException, true);
        log.info("returnService DuplicateKeyException 8000 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("8999");
        thrown = assertThrows(DatabaseException.class, () -> {
            mmsAgentDomainService.returnService("8999");
        });
        assertEquals(thrown instanceof DatabaseException, true);
        log.info("returnService DatabaseException 8999 End");

        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("9998");
        thrown = assertThrows(MmsServiceException.class, () -> {
            mmsAgentDomainService.returnService("9998");
        });
        assertEquals(thrown instanceof MmsServiceException, true);
        log.info("returnService MmsServiceException 9998 End");


        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn("9999");
        thrown = assertThrows(RuntimeException.class, () -> {
            mmsAgentDomainService.returnService("9999");
        });
        assertEquals(thrown instanceof RuntimeException, true);
        log.info("returnService RuntimeException 9999 End");
    }

}