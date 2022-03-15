package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.client.HttpPushDomainClient;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import com.lguplus.fleta.util.HttpPushSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Http SinglePush Component
 *
 * 단건 푸시등록
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HttpSinglePushDomainService {

    private final HttpPushDomainClient httpPushDomainClient;

    private final HttpPushSupport httpPushSupport;

    @Value("${multi.push.reject.regList}")
    private String rejectReg;


    /**
     * 단건푸시등록
     *
     * @param httpPushSingleRequestDto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushSingle(HttpPushSingleRequestDto httpPushSingleRequestDto) {
        log.debug("httpPushSingleRequestDto ::::::::::::::: {}", httpPushSingleRequestDto);

        // 발송 제외 가번 확인
        log.debug("rejectReg :::::::::::::::::::: {}", rejectReg);
        String[] rejectRegIds = rejectReg.split("\\|");
        String regId = httpPushSingleRequestDto.getUsers().get(0);

        if (Arrays.asList(rejectRegIds).contains(regId.strip())) {
            Pair<String, String> cdMsgMap = httpPushSupport.getHttpServiceProps().getExceptionCodeMessage("ExclusionNumberException");

            HttpPushCustomException httpPushCustomException = new HttpPushCustomException();
            httpPushCustomException.setCode(cdMsgMap.getLeft());
            httpPushCustomException.setMessage(cdMsgMap.getRight());

            throw httpPushCustomException;   // 9998 발송제한번호
        }

        String applicationId = httpPushSingleRequestDto.getApplicationId();
        String serviceId = httpPushSingleRequestDto.getServiceId();
        String pushType = httpPushSingleRequestDto.getPushType();
        String message = httpPushSingleRequestDto.getMessage();
        List<String> items = httpPushSingleRequestDto.getItems();

        Map<String, Object> paramMap = httpPushSupport.makePushParameters(applicationId, serviceId, pushType, message, regId, items);

        httpPushDomainClient.requestHttpPushSingle(paramMap);

        // 성공
        return HttpPushResponseDto.builder().build();
    }

}
