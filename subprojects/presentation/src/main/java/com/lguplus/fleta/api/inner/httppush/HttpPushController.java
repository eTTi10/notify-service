package com.lguplus.fleta.api.inner.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.mapper.HttpPushSingleMapper;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.data.vo.HttpPushSingleRequestVo;
import com.lguplus.fleta.service.httppush.HttpPushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@Api(tags = "HttpPush", description = "단건, 멀티 푸시등록")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notify")
public class HttpPushController {

    private final HttpPushService httpPushService;

    private final HttpPushSingleMapper httpPushSingleMapper;


    /**
     * 단건푸시등록
     *
     * @param httpPushSingleRequestVo 단건푸시등록을 위한 VO
     * @return 단건푸시등록 결과 응답
     */
    @ApiOperation(value="단건푸시등록", notes="단건푸시를 등록한다.")
    /*@ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="app_id",       value="순번: 1<br>자리수: 256<br>설명: 어플리케이션 ID", example="lguplushdtvgcm"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="service_id",   value="순번: 2<br>자리수: 5<br>설명: 서비스 ID", example="30015"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="push_type",    value="순번: 3<br>자리수: 1<br>설명: Push 발송 타입(G: 안드로이드, A: 아이폰)", example="G", allowableValues = "g,a,G,A", defaultValue = "G"),
            @ApiImplicitParam(paramType="query", dataType="java.util.List<String>",  required=true,  name="users",        value="순번: 4<br>자리수: 15<br>설명: 사용자 ID", example="01099991234"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="msg",          value="순번: 5<br>자리수: 5<br>설명: 보낼 메시지", example="보낼 메시지"),
            @ApiImplicitParam(paramType="query", dataType="java.util.List<String>", required=false, name="items",        value="순번: 6<br>설명: 추가할 항목 입력", example="badge!^1, sound!^ring.caf, cm!^aaaa")
    })*/
    @PostMapping(value = "/httppush/single")
    public InnerResponseDto<HttpPushResponseDto> requestHttpPushSingle(@RequestBody @Valid HttpPushSingleRequestVo httpPushSingleRequestVo) {
        log.debug("==================단건푸시등록 BEGIN======================");
//        HttpPushSingleRequestDto httpPushSingleRequestDto = httpPushSingleRequestVo.convert();
        HttpPushSingleRequestDto httpPushSingleRequestDto = httpPushSingleMapper.toDto(httpPushSingleRequestVo);

        log.debug("mapstruct httpPushSingleRequestDto :::::::::::::::::::::::::: {}", httpPushSingleRequestDto);

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

}
