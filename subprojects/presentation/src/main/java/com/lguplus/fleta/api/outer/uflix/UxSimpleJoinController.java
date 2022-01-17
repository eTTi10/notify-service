package com.lguplus.fleta.api.outer.uflix;

import com.lguplus.fleta.data.dto.request.outer.UxSimpleJoinSmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.mapper.UxSimpleJoinSmsMapper;
import com.lguplus.fleta.data.vo.UxSimpleJoinSmsRequestVo;
import com.lguplus.fleta.service.uflix.UxSimpleJoinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(tags = "tvG 유플릭스 간편 가입 안내 SMS 요청")
@Slf4j
@RestController
@RequiredArgsConstructor
public class UxSimpleJoinController {

    private final UxSimpleJoinService uxSimpleJoinService;

    private final UxSimpleJoinSmsMapper uxSimpleJoinSmsMapper;

    /**
     * tvG 유플릭스 간편 가입 안내 SMS 요청
     *
     * @param uxSimpleJoinSmsRequestVo tvG 유플릭스 간편 가입 안내 SMS 요청을 위한 VO
     * @return tvG 유플릭스 간편 가입 안내 SMS 요청 결과 응답
     */
    @ApiOperation(value="tvG 유플릭스 간편 가입 안내 SMS 요청", notes="tvG 유플릭스 간편 가입 안내 SMS 요청한다.")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sa_id",        value="순번: 1<br>자리수: 12<br>설명:가입번호", example = "500058151453"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="stb_mac",      value="순번: 2<br>자리수: 20<br>설명: 맥주소", example="001c.627e.039c"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="ctn",          value="순번: 3<br>자리수: 11<br>전화번호" , example="01055805424"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="app_name",     value="순번: 4<br>자리수: <br>설명: 통합 통계용 서비스명", example=""),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="ui_version",   value="순번: 5<br>자리수: <br>설명: 통합 통계용 UI 버전", example=""),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="pre_page",     value="순번: 6<br>자리수: <br>설명: 통합 통계용 이전 페이지<br>메뉴 ID", example=""),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="cur_page",     value="순번: 7<br>자리수: <br>설명: 통합 통계용 현재 페이지<br>메뉴 ID", example=""),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="dev_info",     value="순번: 8<br>자리수: <br>설명: 통합 통계용 접속 단말 타입<br>ex) PHONE, PAD, PC, TV, STB", example="STB"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="os_info",      value="순번: 9<br>자리수: <br>설명: 통합 통계용 OS 정보<br>ex) android_1.5, android_2.2, android_2.3.22, ios_5, ios_6, window_xp, window_7", example="android_8.0.0"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="nw_info",      value="순번: 10<br>자리수: <br>설명: 통합 통계용 접속 네트워크 정보<br>ex) 3G, 4G, 5G, WIFI, WIRE, ETC", example="WIRE"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="dev_model",    value="순번: 11<br>자리수: <br>설명: 통합 통계용 단말 모델명<br>ex) LE-E250", example="S60UPI"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="carrier_type", value="순번: 12<br>자리수: <br>설명: 통합 통계용 통신사 구분<br>ex) L:LGU+, K:KT, S:SKT, E:etc", example="L")})
    @GetMapping("/smartux/UXSimpleJoin")
    public SuccessResponseDto requestUxSimpleJoinSms(@ApiIgnore @Valid UxSimpleJoinSmsRequestVo uxSimpleJoinSmsRequestVo) {
        log.debug("==================requestUxSimpleJoinSms BEGIN======================");

        UxSimpleJoinSmsRequestDto uxSimpleJoinSmsRequestDto = uxSimpleJoinSmsMapper.toDto(uxSimpleJoinSmsRequestVo);

        uxSimpleJoinService.requestUxSimpleJoinSms(uxSimpleJoinSmsRequestDto);

        log.debug("==================requestUxSimpleJoinSms END======================");

        return SuccessResponseDto.builder().build();
    }

}
