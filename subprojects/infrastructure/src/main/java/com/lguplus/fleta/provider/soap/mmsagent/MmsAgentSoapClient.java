package com.lguplus.fleta.provider.soap.mmsagent;

import com.lguplus.fleta.client.MmsAgentDomainClient;
import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import com.lguplus.fleta.provider.soap.mmsagent.module.*;
import com.lguplus.fleta.provider.soap.mmsagent.module.content.BasicContent;
import com.lguplus.fleta.provider.soap.mmsagent.module.content.UplusContent;
import com.lguplus.fleta.provider.soap.mmsagent.module.inf.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
/**
 * ################### 개발중 입니다. 잠시 개발 중단 상태입니다. 리뷰대상이 아닙니다. #####################
 */
public class MmsAgentSoapClient implements MmsAgentDomainClient {
    private Map<String, ?> mms;//설정 및 속성

    @Override
    public String sendMMS(Map<String, ?> mmsConfig, MmsRequestDto mmsDto){
        return Integer.toString(1000);
    }
}
