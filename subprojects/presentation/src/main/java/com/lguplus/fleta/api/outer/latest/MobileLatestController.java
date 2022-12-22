package com.lguplus.fleta.api.outer.latest;

import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.MobileLatestPostRequestVo;
import com.lguplus.fleta.service.latest.MobileLatestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "모바일 최신회차 알림")
@Slf4j
@RestController
@RequestMapping("/mobile/hdtv")
@RequiredArgsConstructor
public class MobileLatestController {

    private final MobileLatestService mobileLatestService;

    @ApiOperation(value = "최신회차 알림 등록", notes = "TV다시보기, 시리즈 VOD의 최신회 알림을 등록하기 위한 인터페이스이다.")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(paramType = "query", dataType = "string", required = true, name = "sa_id", value = "순번: 1<br/>자리수: 12<br/>설명: 가입자 번호", example = "500058151453"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = true, name = "stb_mac", value = "순번: 2<br/>자리수: 20<br/>설명: 맥 어드레스", example = "001c.627e.039c"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "ctn", value = "순번: 3<br/>자리수: 11<br/>설명: 가입자 전화번호", example = "01055805424"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = true, name = "cat_id", value = "순번: 4<br/>자리수: 5<br/>설명: 카테고리 ID", example = "T3021"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = true, name = "cat_name", value = "순번: 5<br/>자리수: 200<br/>설명: 카테고리명", example = "놀라운 대회 스타킹"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = true, name = "reg_id", value = "순번: 6<br/>자리수: 64<br/>설명: PUSH할 REG ID", example = "500058151453"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "service_type", value = "순번: 7<br/>자리수: 1<br/>설명: 서비스 타입<br/>ex) NULL OR V : 모바일tv, R : VR", example = "V"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "access_key", value = "<br>설명: OpenAPI 개발자 Access Key", example = "HDTVoa701"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "cp_id", value = "<br>설명: OpenAPI 개발자 CP ID", example = "cp")
    })
    @PostMapping(value = {"/v1/latest", "/comm/latest"})
    public CommonResponseDto insertLatest(@Valid MobileLatestPostRequestVo request) {
        mobileLatestService.insertLatest(request.convert());
        return SuccessResponseDto.builder().build();
    }
}
