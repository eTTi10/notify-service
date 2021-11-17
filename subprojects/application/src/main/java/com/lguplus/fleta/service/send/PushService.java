package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.service.httppush.HttpPushSingleDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {

    private final PushDomainService pushDomainService;
    private final HttpPushSingleDomainService httpPushSingleDomainService;
    private final HttpPushSingleRequestDto httpPushSingleRequestDto;
    private final SuccessResponseDto successResponseDto;

    public SuccessResponseDto sendPushCode(SendPushCodeRequestDto sendPushCodeRequestDto) {

        String status = httpPushSingleDomainService.requestHttpPushSingle(httpPushSingleRequestDto);

        return successResponseDto;
    }

}
