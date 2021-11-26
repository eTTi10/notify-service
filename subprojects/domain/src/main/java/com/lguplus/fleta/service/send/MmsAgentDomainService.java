package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MmsAgentDomainService {
    private final CallSettingDomainClient aipClient;

    @Value("${mms.setting.rest_sa_id}")
    private String saId;
    @Value("${mms.setting.rest_stb_mac}")
    private String stbMac;
    @Value("${mms.setting.rest_svc_type}")
    private String svcType;
    @Value("${mms.setting.rest_code_id}")
    private String codeId;

    public SuccessResponseDto sendMmsCode(SendMMSRequestDto sendMMSRequestDto) {

        CallSettingRequestDto prm = CallSettingRequestDto.builder().build();
        prm.setSaId(saId);//MMS:mms SMS:sms
        prm.setStbMac(stbMac);//MMS:mms SMS:sms
        prm.setSvcType(svcType);//MMS:E SMS:I
        if(StringUtils.isEmpty(sendMMSRequestDto.getMmsCd())){
            prm.setCodeId(codeId);
        }else{
            prm.setCodeId(sendMMSRequestDto.getMmsCd());
        }
        log.info("001 MmsAgentDomainService");
        CallSettingResultMapDto result = aipClient.callSettingApi(prm);
        log.info("002 MmsAgentDomainService");
        return SuccessResponseDto.builder().build();
    }
}
