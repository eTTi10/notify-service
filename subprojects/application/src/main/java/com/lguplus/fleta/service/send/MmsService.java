package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.service.mmsagent.MmsAgentDomainService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
/**
 * ################### 개발중 입니다. 잠시 개발 중단 상태입니다. 리뷰대상이 아닙니다. #####################
 */
public class MmsService {

    private final MmsAgentDomainService mmsAgentDomainService;

    public SuccessResponseDto sendMms(SendMmsRequestDto request) {

        return mmsAgentDomainService.sendMmsCode(request);
    }

}
