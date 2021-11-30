package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushSingleDomainClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushSingleDomainService {

    private final PushConfig pushConfig;
    private final PushSingleDomainClient pushSingleDomainClient;

    /**
     * 단건푸시등록
     *
     * @param pushRequestSingleDto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public PushClientResponseDto requestPushSingle(PushRequestSingleDto pushRequestSingleDto) {
        log.debug("PushRequestSingleDto ::::::::::::::: {}", pushRequestSingleDto);

        return PushClientResponseDto.builder().build();
    }

}
