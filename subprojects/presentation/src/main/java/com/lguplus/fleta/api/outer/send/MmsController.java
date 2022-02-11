package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.mapper.SendMmsRequestMapper;
import com.lguplus.fleta.data.vo.SendMmsVo;
import com.lguplus.fleta.service.send.MmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "MMS 발송요청")
@Slf4j
@RequiredArgsConstructor
@RestController
public class MmsController {
    private final SendMmsRequestMapper sendMmsRequestMapper;
    private final MmsService smsService;


    @ApiOperation(value="MMS 발송요청", notes="MMS발송을 Agent Server에 요청한다.")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sa_id",       value="순번: 1<br>자리수: 12<br>설명:가입번호", example = "M15030600001"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="stb_mac",     value="순번: 2<br>자리수: 20<br>설명: 가입자 STB MAC Address", example="v150.3060.0001"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="mms_cd",      value="순번: 3<br>자리수: 4<br>설명: MMS 메시지 코드<br>ex) M011, M012" , example="M011"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="ctn",         value="순번: 4<br>자리수: 20<br>설명: 발송대상 번호", example="01051603997"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="replacement", value="순번: 5<br>자리수: 100<br>설명: 대체문구", example="예) 김철수|냉장고")})
    @PostMapping("/mims/sendMms")
    public SuccessResponseDto setPayment(@Valid SendMmsVo vo){
        SendMmsRequestDto sendMmsRequestDto = sendMmsRequestMapper.toDto(vo);
        return smsService.sendMms(sendMmsRequestDto);
    }

}
