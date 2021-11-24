package com.lguplus.fleta.service.mmsagent;

import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.service.send.MMSAgentDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MMSAgentService {


    private final MMSAgentDomainService mmsAgentDomainService;

    public SuccessResponseDto sendMMS(SendMMSRequestDto sendSMSRequestDto) {

        return mmsAgentDomainService.sendMMS(sendSMSRequestDto);

    }

}
