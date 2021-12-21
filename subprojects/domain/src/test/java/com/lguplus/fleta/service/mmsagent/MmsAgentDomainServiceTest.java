package com.lguplus.fleta.service.mmsagent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.client.MmsAgentDomainClient;
import com.lguplus.fleta.config.MmsAgentConfig;
import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Mock
    MmsAgentConfig mmsAgentConfig;


    private Map<String, ?> mmsConfig;//yml파일 mms
    private Map<String, Object> settingConfig;//yml파일 setting

    private static final String saId = "M14070200159";
    private static final String stbMac = "9893.cc1f.e11c";
    private static final String mmsCd = "M011";
    private static final String ctn = "01025851531";
    private static final String replacement = "영희|컴퓨터";
    private static final String svcType = "E";

    @BeforeEach
    void setUp() {
        Map<String, Object> mms = new HashMap<>();
        mms.put("X_Kmms_SVCCODE", 1);
        mms.put("X_Kmms_TextInput", 1);
        mms.put("X_Kmms_redistribution", "1.0");
        mms.put("callback", "16447000");
        mms.put("connecttimeout", 3000);
        mms.put("debug_mode", "N");
        mms.put("namespace", "http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-2");
        mms.put("prefix", "mm7");
        mms.put("readtimeout", 5000);
        mms.put("sender", "01057404003");
        mms.put("server_url", "http://61.101.246.151:888");
        mms.put("vasid", "MMSTVMOBILE");
        mms.put("vaspid", "MMSTVMOBILE");
        mms.put("version", "5.3.0");

        Map<String, Object> setting = new HashMap<>();
        setting.put("request_method","GET");
        setting.put("timeout", 5000);
        setting.put("url", "http://hdtv.suxm.uplus.co.kr/hdtv/comm/setting?sa_id=mms&stb_mac=mms&code_id=&svc_type=E");
        setting.put("rest_url", "http://hdtv.suxm.uplus.co.kr");
        setting.put("rest_path", "/hdtv/comm/setting");
        setting.put("rest_sa_id", "mms");
        setting.put("rest_stb_mac","mms");
        setting.put("rest_code_id","");
        setting.put("rest_svc_type","E");

        mms.put("setting", setting);
        mmsAgentConfig.setMms(mms);

        mmsAgentDomainService = new MmsAgentDomainService(apiClient, mmsAgentConfig, mmsSoap);
    }

    @Test
    void getMmsCallSettingApi() {
        CallSettingDto dto = CallSettingDto.builder()
                .codeId("M011")
                .codeName("U+아이들나라는 네이버 예약과 함께 매 주 아이들과 함께 하기 좋은 체험 장소를 소개합니다.")
                .build();
        List<CallSettingDto> rs = new ArrayList<>();
        rs.add(dto);

        CallSettingResultDto result = CallSettingResultDto.builder()
                .recordset(rs)
                .build();

        CallSettingResultMapDto callSettingDto = CallSettingResultMapDto.builder()
                .result(result)
                .build();

        given( apiClient.mmsCallSettingApi(any())).willReturn(callSettingDto);


       // CallSettingDto setingDto = mmsAgentDomainService.(saId, "mms", mmsCd, "E");


//        /** 응답코드 */
//        private String flag;
//
//        /** 응답메시지 */
//        private String message;
//
//        /** 레코드수 */
//        @JsonProperty("total_count")
//        @Builder.Default private int totalCount = 0;
//
//
//        /** 사용자그룹 */
//        @JsonProperty("memberGroup")
//        private String memberGroup;
//
//        /** 리스트 */
//        @JsonProperty("recordset")
//        private List<CallSettingDto> recordset;

        // SuccessResponseDto responseDto = mmsAgentDomainService.sendMmsCode(sa_id, "mms", mms_cd, "E");
        /*

        //setting API 호출관련 파라메타 셋팅
        CallSettingRequestDto prm = CallSettingRequestDto.builder()
                .saId(saId)//ex) MMS:mms SMS:sms
                .stbMac(stbMac)//ex) MMS:mms SMS:sms
                .codeId(codeId)//ex) M011
                .svcType(svcType)//ex) MMS:E SMS:I
                .build();
        //setting API 호출하여 캐시에 메세지 등록후 출력
        CallSettingResultMapDto callSettingApi = apiClient.mmsCallSettingApi(prm);




        //setting API 호출관련 파라메타 셋팅
        CallSettingRequestDto prm = CallSettingRequestDto.builder()
                .saId(saId)//ex) MMS:mms SMS:sms
                .stbMac(stbMac)//ex) MMS:mms SMS:sms
                .codeId(mmsCd)//ex) M011
                .svcType(svcType)//ex) MMS:E SMS:I
                .build();
        //setting API 호출하여 캐시에 메세지 등록후 출력
        CallSettingResultMapDto callSettingApi = apiClient.mmsCallSettingApi(prm);

         */
    }

    @Test
    void sendMmsCode() {
        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
                .saId(saId)
                .stbMac(stbMac)
                .mmsCd(mmsCd)
                .ctn(ctn)
                .replacement(replacement)
                .build();

        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();

        MmsRequestDto mmsDto = MmsRequestDto.builder()
                .ctn(sendMmsRequestDto.getCtn())
                .mmsTitle(sendMmsRequestDto.getMmsCd())
                .mmsMsg("MMS메세지")//메세지
                .mmsRep(sendMmsRequestDto.getCtn())
                .build();

        String statusMessage = "";

        given( mmsSoap.sendMMS(mmsConfig, mmsRequestDto)).willReturn(statusMessage);

        SuccessResponseDto responseDto = mmsAgentDomainService.sendMmsCode(sendMmsRequestDto);
        Assertions.assertTrue("1000".equals(responseDto.getFlag()));

    }



}