package com.lguplus.fleta.api.inner.musicshow;

import com.lguplus.fleta.data.dto.GetPushResponseDto;
import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.vo.musicshow.PushRequestVo;
import com.lguplus.fleta.service.musicshow.MusicShowService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/notify")
@AllArgsConstructor
public class MusicShowInnerController {

    private final MusicShowService service;

    @ApiOperation(value = "알람 여부 조회")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "sa_id", value = "순번: 1<br>자리수: 12<br>설명:가입번호", example = "500004587606"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "stb_mac", value = "순번: 2<br>자리수: 20<br>설명: 가입자 STB MAC Address", example = "v000.0458.7606"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "album_id", value = "순번: 3<br>자리수: 15<br>설명: 앨범ID", example = "M0118C3162PPV00")
    })
    @GetMapping(value = "/getMusicShowPush")
    public InnerResponseDto<GetPushResponseDto> getPush(@Valid PushRequestVo requestVo) {
        PushRequestDto requestDto = requestVo.makeRefinedGetRequest();

        GetPushResponseDto responseDto = service.getPush(requestDto);

        return InnerResponseDto.of(responseDto);
    }

}
