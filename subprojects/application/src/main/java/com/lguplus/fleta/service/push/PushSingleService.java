package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.inner.PushSingleRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushSingleService {

    private final PushSingleDomainService pushSingleDomainService;

    /**
     * 단건푸시등록
     *
     * @param pushSingleRequestDto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public String requestPushSingle(PushSingleRequestDto pushSingleRequestDto) {
        return pushSingleDomainService.requestPushSingle(pushSingleRequestDto);
    }

}
