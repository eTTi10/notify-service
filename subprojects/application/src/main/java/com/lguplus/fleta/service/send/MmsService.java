package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.service.mmsagent.MmsAgentDomainService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class MmsService {

    private final MmsAgentDomainService mmsAgentDomainService;

    public SuccessResponseDto sendMms(SendMmsRequestDto request) {
        return mmsAgentDomainService.sendMmsCode(request);
    }

}
