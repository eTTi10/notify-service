package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MmsService {

    private final MmsAgentDomainService mmsAgentDomainService;

    public SuccessResponseDto sendMMS(SendMMSRequestDto request) {

        return mmsAgentDomainService.sendMmsCode(request);
    }


}
