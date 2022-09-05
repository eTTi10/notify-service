package com.lguplus.fleta.api.outer.push;

import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.DeviceInfoDeleteRequestVo;
import com.lguplus.fleta.data.vo.DeviceInfoPostRequestVo;
import com.lguplus.fleta.data.vo.DeviceInfoPutRequestVo;
import com.lguplus.fleta.service.push.DeviceInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "단말 정보 등록, 수정, 삭제")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/hdtv/v1/push/deviceinfo")
public class DeviceInfoController {
    private final DeviceInfoService deviceInfoService;

    /**
     * 단말 정보 등록
     *
     * @param deviceInfoRequestVo deviceinfo 등록,수정, 삭제를 위한 VO
     * @return 등록 결과 응답
     */
    @ApiOperation(value="deviceinfo 등록", notes="deviceinfo를 등록한다.")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sa_id",   value="<br>자리수: 12<br>설명:가입번호", example = "500058151453"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true, name="service_type", value="<br>자리수: 1<br>설명: service_type <br> ex) H : HDTV / U : 유플릭스 /  C : 뮤직공연 / R : VR / G : 골프 / D : 게임방송 / B : 프로야구 / K : 아이들나라", example="H"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true, name="agent_type", value="<br>자리수: 1<br>설명: agent_type<br> ex) G:GCM, A:APNS", example="G"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false, name="noti_type", value="<br>자리수: 1<br>설명: noti_type<br> ex) A:전체받기/ S:구독만받기 / N:푸시 안받기", example="N"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false,  name="stb_mac", value="<br>자리수: 20<br>설명: 맥주소", example="001c.627e.039c"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false,  name="access_key", value="<br>설명: OpenAPI 개발자 Access Key", example="HDTVoa701"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false,  name="cp_id", value="<br>설명: OpenAPI 개발자 CP ID", example="cp")
    })
    @PostMapping
    public SuccessResponseDto postDeviceInfo(
            @Validated DeviceInfoPostRequestVo deviceInfoRequestVo){
        deviceInfoService.createDeviceInfo(deviceInfoRequestVo.convert());
        return SuccessResponseDto.builder().build();
    }

    /**
     * 단말 정보 수정(noti_type만 수정)
     *
     * @param deviceInfoRequestVo deviceinfo 등록,수정, 삭제를 위한 VO
     * @return 수정 결과 응답
     */
    @ApiOperation(value="deviceinfo 수정", notes="deviceinfo를 수정한다.")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sa_id",   value="<br>자리수: 12<br>설명:가입번호", example = "500058151453"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true, name="service_type", value="<br>자리수: 1<br>설명: service_type <br> ex) H : HDTV / U : 유플릭스 /  C : 뮤직공연 / R : VR / G : 골프 / D : 게임방송 / B : 프로야구 / K : 아이들나라", example="H"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true, name="agent_type", value="<br>자리수: 1<br>설명: agent_type<br> ex) G:GCM, A:APNS", example="G"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true, name="noti_type", value="<br>자리수: 1<br>설명: noti_type<br> ex) A:전체받기/ S:구독만받기 / N:푸시 안받기", example="N"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false,  name="stb_mac", value="<br>자리수: 20<br>설명: 맥주소", example="001c.627e.039c"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false,  name="access_key", value="<br>설명: OpenAPI 개발자 Access Key", example="HDTVoa701"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false,  name="cp_id", value="<br>설명: OpenAPI 개발자 CP ID", example="cp")
    })
    @PutMapping
    public SuccessResponseDto putDeviceInfo(
            @Validated DeviceInfoPutRequestVo deviceInfoRequestVo){
        deviceInfoService.updateDeviceInfo(deviceInfoRequestVo.convert());
        return SuccessResponseDto.builder().build();
    }

    /**
     * 단말 정보 삭제
     *
     * @param deviceInfoRequestVo deviceinfo 등록,수정, 삭제를 위한 VO
     * @return 삭제 결과 응답
     */
    @ApiOperation(value="deviceinfo 삭제", notes="deviceinfo를 삭제한다.")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType="query", dataType="string", required=true,  name="sa_id",   value="<br>자리수: 12<br>설명:가입번호", example = "500058151453"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true, name="service_type", value="<br>자리수: 1<br>설명: service_type <br> ex) H : HDTV / U : 유플릭스 /  C : 뮤직공연 / R : VR / G : 골프 / D : 게임방송 / B : 프로야구 / K : 아이들나라", example="H"),
            @ApiImplicitParam(paramType="query", dataType="string", required=true, name="agent_type", value="<br>자리수: 1<br>설명: agent_type<br> ex) G:GCM, A:APNS", example="G"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false,  name="stb_mac", value="<br>자리수: 20<br>설명: 맥주소", example="001c.627e.039c"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false,  name="access_key", value="<br>설명: OpenAPI 개발자 Access Key", example="HDTVoa701"),
            @ApiImplicitParam(paramType="query", dataType="string", required=false,  name="cp_id", value="<br>설명: OpenAPI 개발자 CP ID", example="cp")
    })
    @DeleteMapping
    public SuccessResponseDto deleteDeviceInfo(
            @Validated DeviceInfoDeleteRequestVo deviceInfoRequestVo){
        deviceInfoService.deleteDeviceInfo(deviceInfoRequestVo.convert());
        return SuccessResponseDto.builder().build();
    }
}
