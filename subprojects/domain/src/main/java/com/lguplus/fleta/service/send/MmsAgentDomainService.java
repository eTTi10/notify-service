package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MmsAgentDomainService {
    private final CallSettingDomainClient aipClient;

    public SuccessResponseDto sendMmsCode(SendMMSRequestDto sendMMSRequestDto) {
        CallSettingRequestDto prm = CallSettingRequestDto.builder().build();
        prm.setSaId("mms");
        prm.setStbMac("mms");
        prm.setSvcType("E");
        prm.setCodeId(sendMMSRequestDto.getMmsCd());

        Map<String, Object> result = aipClient.callSettingApi(prm);
        return SuccessResponseDto.builder().build();
    }
}
