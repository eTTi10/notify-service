package com.lguplus.fleta.api.outer.send;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SendPushResponseDto;
import com.lguplus.fleta.data.vo.SendPushCodeRequestBodyVo;
import com.lguplus.fleta.data.vo.SendPushCodeRequestVo;
import com.lguplus.fleta.service.send.PushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "sendPushCode")
@Slf4j
@RequiredArgsConstructor
@RestController
public class PushController {

    private final PushService pushService;
    private final MappingJackson2XmlHttpMessageConverter xmlHttpMessageConverter;
    private final LocalValidatorFactoryBean validator;

    /**
     * MIMS.IPTV058 Code를 통한 푸시발송 요청
     *
     * @param
     * @return
     */
    @ApiOperation(value = "Code를 통한 푸시발송 요청", notes = "입력받은 Code를 이용해 Agent에 푸시 발송을 요청한다.")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(paramType = "query", dataType = "string", required = true, name = "sa_id", value = "순번: 1<br/>자리수: 8~15<br/>설명:가입자정보", example = "500058151453"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = true, name = "stb_mac", value = "순번: 2<br/>자리수: 10~20<br/>설명: 가입자 STB MAC", example = "001c.627e.039c"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = true, name = "reg_id", value = "순번: 3<br/>자리수: 12<br/>설명: 발송ID", example = "M00020202284"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = true, name = "push_type", value = "순번: 4<br/>자리수: 1~5<br/>설명: Push발송 타입G : GCM / A : APNS / L : LG Push / 여러 타입을 동시 발송 시 ‘|’로 구분", example = "G|A"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = true, name = "send_code", value = "순번: 5<br/>자리수: 4<br/>설명: 발송코드", example = "P001"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = true, name = "reg_type", value = "순번: 5<br/>자리수: 1<br/>설명: 발송ID 타입", example = "1"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "service_type", value = "순번: 5<br/>자리수: 5<br/>설명: Push 대상 타입<br/>ex) U+tv : TV / 모바일tv : H / 프로야구 : B / 아이들나라 : K / 골프 : O / 아이돌Live : C,  ‘|’구분자를 통해 멀티 선택 가능", example = "")})
    @PostMapping(value = "/mims/sendPushCode")
    public SendPushResponseDto sendPushCode(
        @RequestBody String requestBodyStr,
        @ApiIgnore @Valid SendPushCodeRequestVo sendPushCodeRequestVo
    ) throws JsonProcessingException {

        log.debug("body:{}", requestBodyStr);

        SendPushCodeRequestBodyVo sendPushCodeRequestBodyVo = xmlHttpMessageConverter
            .getObjectMapper().readValue(requestBodyStr, SendPushCodeRequestBodyVo.class);

        SendPushCodeRequestDto sendPushCodeRequestDto = sendPushCodeRequestBodyVo.convert(sendPushCodeRequestVo, requestBodyStr);

        return pushService.sendPushCode(sendPushCodeRequestDto);

    }
}
