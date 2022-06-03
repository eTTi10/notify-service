package com.lguplus.fleta.api.outer.musicshow;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "뮤직공연 콘서트 LIVE 알람")
@RestController
public class MusicshowController {

    @ApiOperation(value = "알람 여부 조회")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "sa_id",    value = "순번: 1<br>자리수: 12<br>설명:가입번호", example = "M15030600001"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "stb_mac",  value = "순번: 2<br>자리수: 20<br>설명: 가입자 STB MAC Address", example="v150.3060.0001"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "album_id", value = "순번: 3<br>자리수: 15<br>설명: 앨범ID", example="")
    })
    @GetMapping("/musicshow/push")
    public String getPush(){
        return null;
    }

    @ApiOperation(value = "알람 여부 등록")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "sa_id",    value = "순번: 1<br>자리수: 12<br>설명:가입번호", example = "M15030600001"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "stb_mac",  value = "순번: 2<br>자리수: 20<br>설명: 가입자 STB MAC Address", example="v150.3060.0001"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "category_id", value = "순번: 3<br>자리수: 5<br>설명: 카테고리ID", example=""),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "album_id", value = "순번: 4<br>자리수: 15<br>설명: 앨범ID", example=""),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "album_nm", value = "순번: 5<br>자리수: <br>설명: 앨범명", example=""),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "start_dt", value = "순번: 6<br >자리수: 12<br>설명: 공연시작일시", example="201912012359")
    })
    @PostMapping("/musicshow/push")
    public String registerPush(){
        return null;
    }

    @ApiOperation(value = "알람 여부 삭제")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "sa_id",    value = "순번: 1<br>자리수: 12<br>설명:가입번호", example = "M15030600001"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "stb_mac",  value = "순번: 2<br>자리수: 20<br>설명: 가입자 STB MAC Address", example="v150.3060.0001"),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "category_id", value = "순번: 3<br>자리수: 5<br>설명: 카테고리ID", example=""),
        @ApiImplicitParam(paramType = "query", dataType = "string", required = false, name = "album_id", value = "순번: 4<br>자리수: 15<br>설명: 앨범ID", example=""),
    })
    @DeleteMapping("/musicshow/push")
    public String deletePush(){
        return null;
    }
}
