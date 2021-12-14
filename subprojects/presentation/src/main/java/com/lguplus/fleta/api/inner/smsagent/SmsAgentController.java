package com.lguplus.fleta.api.inner.smsagent;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.data.mapper.SendSmsCodeMapper;
import com.lguplus.fleta.data.mapper.SendSmsMapper;
import com.lguplus.fleta.data.vo.SendSmsCodeVo;
import com.lguplus.fleta.data.vo.SendSmsVo;
import com.lguplus.fleta.exception.smsagent.NoHttpsException;
import com.lguplus.fleta.exception.smsagent.ServerSettingInfoException;
import com.lguplus.fleta.service.smsagent.SmsAgentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "SmsAgent", description = "SMS 발송 처리")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/notify")
public class SmsAgentController {

    @Value("${check.https}")
    private String propertyCheckHttps;

    private final SmsAgentService smsAgentService;
    private final SendSmsMapper sendSmsMapper;
    private final SendSmsCodeMapper sendSmsCodeMapper;


    /**
     * sms전송(문자내용을 받아 단순발송)
     * @param requestVo
     * @param request
     * @return SmsGatewayResponseDto
     */
    @ApiOperation(value="SMS 단순 전송", notes="문자내용을 입력받아 SMS Gateway로 전송한다.")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="s_ctn",         value="순번: 1<br>자리수: <br>설명: 수신휴대폰번호", example="01044445555"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="r_ctn",       value="순번: 2<br>자리수: <br>설명: 회신휴대폰번호", example="01044445555"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="msg",     value="순번: 3<br>자리수: 4<br>설명: SMS코드<br>ex) S001, S002" , example="S001")})
    @PostMapping("/smsagent/sms")
    public InnerResponseDto<SmsGatewayResponseDto> sendSms(@ApiIgnore @Valid SendSmsVo requestVo, HttpServletRequest request) {

        // Http통신 체크
        checkHttps(request);

        SendSmsRequestDto requestDto = sendSmsMapper.toDto(requestVo);

        return InnerResponseDto.of(smsAgentService.sendSms(requestDto));
    }

    /**
     * sms전송(Code로 내용조회해서 발송)
     * @param requestVo
     * @return SmsGatewayResponseDto
     */
    @ApiOperation(value="SMS 코드 전송", notes="코드를 이용해 문자내용을 조회하여 SMS Gateway로 전송한다.")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sa_id",         value="순번: 1<br>자리수: 12<br>설명:가입번호", example = "M15030600001"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="stb_mac",       value="순번: 2<br>자리수: 20<br>설명: 맥주소", example="v150.3060.0001"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sms_cd",     value="순번: 3<br>자리수: 4<br>설명: SMS코드<br>ex) S001, S002" , example="S001"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true, name="ctn",     value="순번: 4<br>자리수: <br>설명: 수신휴대폰번호", example="01044445555"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="replacement",   value="순번: 5<br>자리수: <br>설명: 대체문구", example="http://google.com/start/we09gn2ks")})
    @PostMapping("/smsagent/smsCode")
    public InnerResponseDto<SmsGatewayResponseDto> sendSmsCode(@ApiIgnore @Valid SendSmsCodeVo requestVo) {

        SendSmsCodeRequestDto requestDto = sendSmsCodeMapper.toDto(requestVo);

        return InnerResponseDto.of(smsAgentService.sendSmsCode(requestDto));
    }

    /**
     * HTTPS 통신인지 체크
     * @param request
     */
    private void checkHttps(HttpServletRequest request) {

        String checkHttps = StringUtils.defaultString(propertyCheckHttps, "1");
        log.debug("[checkHttps] - [{}]]", checkHttps);
        String protocol = request.getScheme();

        if (!"0".equals(checkHttps)) {

            if (!"https".equalsIgnoreCase(protocol)) {

                throw new NoHttpsException("HTTPS 통신이 아닙니다");
            }
        }
    }
}
