package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
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

@Api(tags = "sendMmsCode", description = "MMS 발송요청")
@Slf4j
@RequiredArgsConstructor
@RestController
public class MmsController {

    private final MmsService smsService;

    /**
     * MMS전송요청
     * mms_cd
     * M001 : 모바일tv 앱 설치안내 문자
     * M002 : 프로야구 앱 설치안내 문자
     * M003 : 아이들나라 앱 설치안내 문자
     * M004 : 골프 앱 설치안내 문자
     * M005 : 아이돌Live 앱 설치안내 문자
     *
     * sa_id:가입자정보
     * stb_mac:가입자 STB MAC Address
     * mms_cd:MMS 메시지 코드
     * ctn:발송대상 번호
     * replacement:치환문자
     *
     * replacement
     * 치환하고자 하는 값 입력
     * 예) 김철수|냉장고
     * #모바일로 페어링 요청 알림 MMS발송일 경우 트랜젝션 ID 입력 됨
     *
     * @param request
     * @return
     * @throws Exception
     */

    /**
     * MIMS.IPTV037 SMS발송요청
     * @param request
     * @return SmsGatewayResponseDto
     */
    @ApiOperation(value="MMS 발송요청", notes="MMS발송을 Agent Server에 요청한다.")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sa_id",       value="순번: 1<br>자리수: 12<br>설명:가입번호", example = "M15030600001"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="stb_mac",     value="순번: 2<br>자리수: 20<br>설명: 가입자 STB MAC Address", example="v150.3060.0001"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="mms_cd",      value="순번: 3<br>자리수: 4<br>설명: MMS 메시지 코드<br>ex) M011, M012" , example="M011"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="ctn",         value="순번: 4<br>자리수: 20<br>설명: 발송대상 번호", example="01051603997"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="replacement", value="순번: 5<br>자리수: 100<br>설명: 대체문구", example="예) 김철수|냉장고")})
    @PostMapping("/mims/sendMms")
    public SuccessResponseDto setPayment(@Valid SendMmsVo request) throws Exception {

        log.debug("SmsController.setPayment() - {}:{}", "MMS발송 요청", request);

        SendMmsRequestDto requestDto = request.convert();

        return smsService.sendMms(requestDto);
    }

}
