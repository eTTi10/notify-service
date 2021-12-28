package com.lguplus.fleta.service.mmsagent;

import com.lguplus.fleta.client.CallSettingDomainClient;
import com.lguplus.fleta.client.MmsAgentDomainClient;
import com.lguplus.fleta.config.MmsAgentConfig;
import com.lguplus.fleta.data.dto.request.MmsRequestDto;
import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.request.inner.CallSettingRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingDto;
import com.lguplus.fleta.data.dto.response.inner.CallSettingResultMapDto;
import com.lguplus.fleta.exception.NoResultException;
import com.lguplus.fleta.exception.mmsagent.*;
import com.lguplus.fleta.exception.mmsagent.ServerSettingInfoException;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.lguplus.fleta.exception.mmsagent.DatabaseException;
import java.lang.NumberFormatException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * ################### 개발중 입니다. 잠시 개발 중단 상태입니다. 리뷰대상이 아닙니다. #####################
 */
public class MmsAgentDomainService {
    private final CallSettingDomainClient apiClient;
    private final MmsAgentConfig config;
    private final MmsAgentDomainClient mmsSoap;

    private Map<String, ?> mmsConfig;//yml파일 mms
    private Map<String, Object> settingConfig;//yml파일 setting


    /**
     * 전송메세지를 취득후 MM7모듈함수를 실행
     * @param sendMmsRequestDto
     * @return
     */
    public SuccessResponseDto sendMmsCode(@NotNull SendMmsRequestDto sendMmsRequestDto) {
        return SuccessResponseDto.builder().build();
    }



}
