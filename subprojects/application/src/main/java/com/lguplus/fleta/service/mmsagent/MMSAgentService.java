package com.lguplus.fleta.service.mmsagent;

import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.service.send.MmsAgentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MMSAgentService {


    private final MmsAgentDomainService mmsAgentDomainService;

    public SuccessResponseDto sendMMS(SendMMSRequestDto sendSMSRequestDto) {

        return mmsAgentDomainService.sendMMS(sendSMSRequestDto);

    }

}
