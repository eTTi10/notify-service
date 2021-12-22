package com.lguplus.fleta.service.push;

import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.exception.push.*;
import com.lguplus.fleta.properties.HttpServiceProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushMultiDomainService {

    private final PushConfig pushConfig;

    private final AtomicInteger tranactionMsgId = new AtomicInteger(0);
    private static final int TRANSACTION_MAX_SEQ_NO = 9999;
    private static final String DATE_FOMAT = "yyyyMMdd";

    /**
     * Multi 푸시등록
     *
     * @param pushRequestMultiDto Multi 푸시등록을 위한 DTO
     * @return Multi 푸시등록 결과
     */
    public PushClientResponseDto requestMultiPush(PushRequestMultiDto pushRequestMultiDto) {
        log.debug("requestMultiPush ::::::::::::::: {}", pushRequestMultiDto);


        return PushClientResponseDto.builder().code("").message("")
                .build();
    }

    private String getTransactionId() {
        if(tranactionMsgId.get() >= TRANSACTION_MAX_SEQ_NO) {
            tranactionMsgId.set(0);
            return DateFormatUtils.format(new Date(), DATE_FOMAT) + String.format("%04d", tranactionMsgId.get());
        }
        return DateFormatUtils.format(new Date(), DATE_FOMAT) + String.format("%04d", tranactionMsgId.incrementAndGet());
    }

}
