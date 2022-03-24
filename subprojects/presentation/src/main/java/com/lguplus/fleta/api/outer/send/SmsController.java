package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SendSmsResponseDto;
import com.lguplus.fleta.data.mapper.SendSmsCodeMapper;
import com.lguplus.fleta.data.vo.SendSmsCodeVo;
import com.lguplus.fleta.service.send.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(tags = "sendSms")
@Slf4j
@RequiredArgsConstructor
@RestController
public class SmsController {

    private final SmsService smsService;
    private final SendSmsCodeMapper sendSmsCodeMapper;

    /**
     * MIMS.IPTV037 SMS발송요청
     * @param request
     * @return SmsGatewayResponseDto
     */
    @ApiOperation(value="SMS 발송요청", notes="SMS발송을 Agent Server에 요청한다.")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sa_id",         value="순번: 1<br>자리수: 12<br>설명:가입번호", example = "M15030600001"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="stb_mac",       value="순번: 2<br>자리수: 20<br>설명: 맥주소", example="v150.3060.0001"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sms_cd",     value="순번: 3<br>자리수: 4<br>설명: SMS코드<br>ex) S001, S002" , example="S001"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true, name="ctn",     value="순번: 4<br>자리수: <br>설명: 수신휴대폰번호", example="01051603997"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="replacement",   value="순번: 5<br>자리수: <br>설명: 대체문구", example="http://google.com/start/we09gn2ks")})
    @PostMapping("/mims/sendSms")
    public SendSmsResponseDto sendSmsCode(@ApiIgnore @Valid SendSmsCodeVo request) {

        SendSmsCodeRequestDto requestDto = sendSmsCodeMapper.toDto(request);

        return smsService.sendSmsCode(requestDto);
    }

}
