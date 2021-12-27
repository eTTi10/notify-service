package com.lguplus.fleta.api.inner.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.mapper.HttpPushMultiMapper;
import com.lguplus.fleta.data.mapper.HttpPushSingleMapper;
import com.lguplus.fleta.data.vo.HttpPushMultiRequestVo;
import com.lguplus.fleta.data.vo.HttpPushSingleRequestVo;
import com.lguplus.fleta.service.httppush.HttpPushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "HttpPush 단건, 멀티 푸시등록")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notify")
public class HttpPushController {

    private final HttpPushService httpPushService;

    private final HttpPushSingleMapper httpPushSingleMapper;

    private final HttpPushMultiMapper httpPushMultiMapper;


    /**
     * 단건푸시등록
     *
     * @param httpPushSingleRequestVo 단건푸시등록을 위한 VO
     * @return 단건푸시등록 결과 응답
     */
    @ApiOperation(value="단건푸시등록", notes="단건푸시를 등록한다.")
    @PostMapping(value = "/httppush/single")
    public InnerResponseDto<HttpPushResponseDto> requestHttpPushSingle(@RequestBody @Valid HttpPushSingleRequestVo httpPushSingleRequestVo) {
        log.debug("==================단건푸시등록 BEGIN======================");
        HttpPushSingleRequestDto httpPushSingleRequestDto = httpPushSingleMapper.toDto(httpPushSingleRequestVo);

        log.debug("mapstruct httpPushSingleRequestDto :::::::::::::::::::::::::: {}", httpPushSingleRequestDto);

        httpPushService.requestHttpPushSingle(httpPushSingleRequestDto);

        log.debug("==================단건푸시등록 END======================");

        // 성공
        return InnerResponseDto.of(HttpPushResponseDto.builder().build());
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
        HttpPushMultiRequestDto httpPushMultiRequestDto = httpPushMultiMapper.toDto(httpPushMultiRequestVo);

        log.debug("mapstruct httpPushMultiRequestDto :::::::::::::::::::::::::: {}", httpPushMultiRequestDto);

        httpPushService.requestHttpPushMulti(httpPushMultiRequestDto);

        log.debug("==================멀티푸시등록 END======================");

        // 성공
        return InnerResponseDto.of(HttpPushResponseDto.builder().build());
    }

}
