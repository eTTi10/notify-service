package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SendPushResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PushService {

    private final PushDomainService pushDomainService;

    public SendPushResponseDto sendPushCode(SendPushCodeRequestDto sendPushCodeRequestDto) {

        return pushDomainService.sendPushCode(sendPushCodeRequestDto);
    }
}
