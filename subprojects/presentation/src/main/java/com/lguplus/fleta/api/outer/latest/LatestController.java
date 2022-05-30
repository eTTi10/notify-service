package com.lguplus.fleta.api.outer.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.mapper.LatestPostRequestMapper;
import com.lguplus.fleta.data.mapper.LatestSearchRequestMapper;
import com.lguplus.fleta.data.vo.LatestPostRequestVo;
import com.lguplus.fleta.data.vo.LatestSearchRequestVo;
import com.lguplus.fleta.service.latest.LatestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "최신회 알림 조회")
@Slf4j
@RestController
@RequiredArgsConstructor
public class LatestController {
    private final LatestSearchRequestMapper latestSearchRequestMapper;
    private final LatestPostRequestMapper latestPostRequestMapper;

    private final LatestService latestService;

    @ApiOperation(value="최신회 알림 조회", notes="최신회 알림을 조회한다.")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sa_id",   value="순번: 1<br>자리수: 12<br>설명:가입번호", example = "500058151453"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="stb_mac", value="순번: 2<br>자리수: 20<br>설명: 맥주소", example="001c.627e.039c"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="ctn",     value="순번: 3<br>자리수: 11<br>전화번호 or 단말 맥 어드레스<br>ex) S001, S002" , example="01055805424"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="cat_id",  value="순번: 4<br>자리수: 5<br>설명: 카테고리 아이디", example="T3021")})
    @GetMapping(value = "/smartux/comm/latest")
    public GenericRecordsetResponseDto<LatestDto> getLatestList(@Valid LatestSearchRequestVo vo) {
        LatestRequestDto latestRequestDto = latestSearchRequestMapper.toDto(vo);
        return latestService.getLatestList(latestRequestDto);
    }

    @ApiOperation(value="최신회 알림 삭제", notes="최신회 알림을 삭제한다.")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sa_id",   value="순번: 1<br>자리수: 12<br>설명:가입번호", example = "500058151453"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="stb_mac", value="순번: 2<br>자리수: 20<br>설명: 맥주소", example="001c.627e.039c"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="ctn",     value="순번: 3<br>자리수: 11<br>전화번호 or 단말 맥 어드레스<br>ex) S001, S002" , example="01055805424"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true, name="cat_id",  value="순번: 4<br>자리수: 5<br>설명: 카테고리 아이디", example="T3021")})
    @DeleteMapping("/smartux/comm/latest")
    public CommonResponseDto deleteLatest(@Valid LatestSearchRequestVo vo) {
        LatestRequestDto latestRequestDto = latestSearchRequestMapper.toDto(vo);
        latestService.deleteLatest(latestRequestDto);
        return SuccessResponseDto.builder().build();
    }

    @ApiOperation(value="최신회 알림 등록", notes="최신회 알림을 등록한다.")
    @ApiImplicitParams(value= {
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sa_id",       value="순번: 1<br>자리수: 12<br>설명:가입번호", example = "500058151453"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="stb_mac",     value="순번: 2<br>자리수: 20<br>설명: 맥주소", example="001c.627e.039c"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="ctn",         value="순번: 3<br>자리수: 11<br>전화번호 or 단말 맥 어드레스<br>ex) S001, S002" , example="01055805424"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="cat_id",      value="순번: 4<br>자리수: 5<br>설명: 카테고리 아이디", example="T3021"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="cat_name",    value="순번: 5<br>자리수: 200<br>설명: 카테고리명", example="놀라운 대회 스타킹"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="reg_id",      value="순번: 6<br>자리수: 64<br>설명: Push 할 Reg ID", example="500058151453"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="category_gb", value="순번: 7<br>자리수: 3<br>설명: 카테고리 구분 - 디폴트 I20", example="I20")
    })
    @PostMapping("/smartux/comm/latest")
    public CommonResponseDto insertLatest(@Valid LatestPostRequestVo vo) {
        LatestRequestDto latestRequestDto = latestPostRequestMapper.toDto(vo);
        latestService.insertLatest(latestRequestDto);
        return SuccessResponseDto.builder().build();
    }
}