package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushMultiService {

    private final PushMultiDomainService pushMultiDomainService;

    /**
     * 단건푸시등록
     *
     * @param pushRequestMultiDto Multi 푸시등록을 위한 DTO
     * @return 푸시등록 결과
     */
    public String requestMultiPush(PushRequestMultiDto pushRequestMultiDto) {
        return pushMultiDomainService.requestMultiPush(pushRequestMultiDto);
    }

}