package com.lguplus.fleta.api.inner.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.mapper.HttpPushSingleMapper;
import com.lguplus.fleta.data.vo.HttpPushSingleRequestVo;
import com.lguplus.fleta.service.httppush.HttpSinglePushService;
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
 * Http SinglePush RestController
 *
 * 단건 푸시등록
 */
@Api(tags = "HttpPush 단건 푸시등록")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notify")
public class HttpSinglePushController {

    private final HttpSinglePushService httpSinglePushService;

    private final HttpPushSingleMapper httpPushSingleMapper;


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

        log.debug("==================단건푸시등록 END======================");

        // 성공
        return InnerResponseDto.of(httpSinglePushService.requestHttpPushSingle(httpPushSingleRequestDto));
    }

}
