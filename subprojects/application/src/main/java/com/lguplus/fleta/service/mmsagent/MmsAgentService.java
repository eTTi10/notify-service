package com.lguplus.fleta.service.mmsagent;

import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MmsAgentService {


    private final MmsAgentDomainService mmsAgentDomainService;

    public SuccessResponseDto sendMms(SendMmsRequestDto sendSMSRequestDto) {

        return mmsAgentDomainService.sendMmsCode(sendSMSRequestDto);

    }

}
