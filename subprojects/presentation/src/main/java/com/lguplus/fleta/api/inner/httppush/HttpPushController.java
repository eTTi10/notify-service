package com.lguplus.fleta.api.inner.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.data.vo.HttpPushMultiRequestVo;
import com.lguplus.fleta.data.vo.HttpPushSingleRequestVo;
import com.lguplus.fleta.service.httppush.HttpPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Http Push RestController
 *
 * 단건, 멀티 푸시등록
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notify")
public class HttpPushController {

    private final HttpPushService httpPushService;


    /**
     * 단건푸시등록
     *
     * @param httpPushSingleRequestVo 단건푸시등록을 위한 VO
     * @return 단건푸시등록 결과 응답
     */
    @PostMapping(value = "/httppush/single")
    public InnerResponseDto<HttpPushResponseDto> requestHttpPushSingle(@RequestBody @Valid HttpPushSingleRequestVo httpPushSingleRequestVo) {
        log.debug("==================단건푸시등록 BEGIN======================");
        HttpPushSingleRequestDto httpPushSingleRequestDto = httpPushSingleRequestVo.convert();

        HttpPushResponseDto httpPushResponseDto = httpPushService.requestHttpPushSingle(httpPushSingleRequestDto);

        log.debug("httpPushResponseDto :::::::::::::::::::: {}", httpPushResponseDto);
        log.debug("==================단건푸시등록 END======================");

        // 성공
        if (httpPushResponseDto.getCode().equals("200")) {
            return InnerResponseDto.of(httpPushResponseDto);
        }

        // 실패
        return InnerResponseDto.of(InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR, httpPushResponseDto);
    }

    /**
     * 멀티푸시등록
     *
     * @param httpPushMultiRequestVo 멀티푸시등록 위한 VO
     * @return 멀티푸시등록 결과 응답
     */
    @PostMapping(value = "/httppush/multi")
    public InnerResponseDto<HttpPushResponseDto> requestHttpPushMulti(@RequestBody @Valid HttpPushMultiRequestVo httpPushMultiRequestVo) {
        log.debug("==================멀티푸시등록 BEGIN======================");
        HttpPushMultiRequestDto httpPushMultiRequestDto = httpPushMultiRequestVo.convert();

        HttpPushResponseDto httpPushResponseDto = httpPushService.requestHttpPushMulti(httpPushMultiRequestDto);

        log.debug("httpPushResponseDto :::::::::::::::::::: {}", httpPushResponseDto);
        log.debug("==================멀티푸시등록 END======================");

        // 성공
        if (httpPushResponseDto.getCode().equals("200")) {
            return InnerResponseDto.of(httpPushResponseDto);
        }

        // 실패
        return InnerResponseDto.of(InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR, httpPushResponseDto);
    }

}
