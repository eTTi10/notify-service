package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MMSService {

    private final MMSAgentDomainService mmsAgentDomainService;

    public SuccessResponseDto sendMMS(SendMMSRequestDto request) {

        return mmsAgentDomainService.sendMMS(request);
    }


}
