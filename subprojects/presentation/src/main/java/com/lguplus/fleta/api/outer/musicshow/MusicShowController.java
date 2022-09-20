package com.lguplus.fleta.api.outer.musicshow;

import com.lguplus.fleta.data.dto.GetPushResponseDto;
import com.lguplus.fleta.data.dto.PostPushResponseDto;
import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.vo.musicshow.PostPushRequestVo;
import com.lguplus.fleta.data.vo.musicshow.PushRequestVo;
import com.lguplus.fleta.service.musicshow.MusicShowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "뮤직공연 콘서트 LIVE 알람")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/videolte/musicshow/push")
public class MusicShowController {

    private final MusicShowService service;

    @ApiOperation(value = "알람 여부 조회")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "sa_id", value = "순번: 1<br>자리수: 12<br>설명:가입번호", example = "500004587606"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "stb_mac", value = "순번: 2<br>자리수: 20<br>설명: 가입자 STB MAC Address", example = "v000.0458.7606"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "album_id", value = "순번: 3<br>자리수: 15<br>설명: 앨범ID", example = "M0118C3162PPV00")
    })
    @GetMapping
    public GetPushResponseDto getPush(@Valid PushRequestVo requestVo) {
        PushRequestDto requestDto = requestVo.makeRefinedGetRequest();

        GetPushResponseDto responseDto = service.getPush(requestDto);

        return responseDto;
    }

    @ApiOperation(value = "알람 여부 등록")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "sa_id", value = "순번: 1<br>자리수: 12<br>설명:가입번호", example = "M15030600001"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "stb_mac", value = "순번: 2<br>자리수: 20<br>설명: 가입자 STB MAC Address", example = "v150.3060.0001"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "category_id", value = "순번: 3<br>자리수: 5<br>설명: 카테고리ID", example = ""),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "album_id", value = "순번: 4<br>자리수: 15<br>설명: 앨범ID", example = ""),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "album_nm", value = "순번: 5<br>자리수: <br>설명: 앨범명", example = ""),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "start_dt", value = "순번: 6<br >자리수: 12<br>설명: 공연시작일시", example = "201912012359")
    })
    @PostMapping
    public PostPushResponseDto registerPush(@Valid PostPushRequestVo requestVo) {
        PushRequestDto requestDto = requestVo.makeRefinedPostRequest();

        PostPushResponseDto responseDto = service.postPush(requestDto);

        return responseDto;
    }

    @ApiOperation(value = "알람 여부 삭제")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "sa_id", value = "순번: 1<br>자리수: 12<br>설명:가입번호", example = "M15030600001"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "stb_mac", value = "순번: 2<br>자리수: 20<br>설명: 가입자 STB MAC Address", example = "v150.3060.0001"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "album_id", value = "순번: 4<br>자리수: 15<br>설명: 앨범ID", example = ""),
    })
    @DeleteMapping
    public PostPushResponseDto releasePush(@Valid PushRequestVo requestVo) {
        PushRequestDto requestDto = requestVo.makeRefinedReleaseRequest();

        PostPushResponseDto responseDto = service.releasePush(requestDto);

        return responseDto;
    }
}
