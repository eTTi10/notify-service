package com.lguplus.fleta.service.mmsagent;

import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MmsAgentService {


    private final MmsAgentDomainService mmsAgentDomainService;

    public SuccessResponseDto sendMms(SendMMSRequestDto sendSMSRequestDto) throws Exception {

        return mmsAgentDomainService.sendMmsCode(sendSMSRequestDto);

    }

}
