package com.lguplus.fleta.provider.external.mmsagent;

import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.provider.external.mmsagent.soap.MmsAgentSoapClient;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith({MockitoExtension.class})
class MmsAgentSoapClientTest {

    private static final String saId = "M14070200159";
    private static final String stbMac = "9893.cc1f.e11c";
    private static final String mmsCd = "M011";
    private static final String ctn = "01025851111";
    private static final String replacement = "영희|컴퓨터";
    private static Map<String, Object> mmsConfig;//yml파일/mms
    private MmsAgentSoapClient mmsAgentSoapClient;

    @BeforeEach
    void setUp() {
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
        mmsAgentSoapClient = new MmsAgentSoapClient();
    }

    @Test
    void testSendMMS() {
        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
            .saId(saId)
            .stbMac(stbMac)
            .mmsCd(mmsCd)
            .ctn(ctn)
            .replacement(replacement)
            .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();
        mmsAgentSoapClient.sendMMS(mmsConfig, mmsRequestDto);
        log.info("End SUCCESS");
    }

    @Test
    void testSendMMS_urlNull() {
        SendMmsRequestDto sendMmsRequestDto = SendMmsRequestDto.builder()
            .saId(saId)
            .stbMac(stbMac)
            .mmsCd(mmsCd)
            .ctn(ctn)
            .replacement(replacement)
            .build();
        MmsRequestDto mmsRequestDto = MmsRequestDto.builder().build();
        mmsConfig.put("server_url", "");
        String resultStr = mmsAgentSoapClient.sendMMS(mmsConfig, mmsRequestDto);
        assertEquals(resultStr, "5200");
        log.info("End urlNull");
    }
}
