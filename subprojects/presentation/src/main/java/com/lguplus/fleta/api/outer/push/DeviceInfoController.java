package com.lguplus.fleta.api.outer.push;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.mapper.DeviceInfoPostRequestMapper;
import com.lguplus.fleta.data.vo.DeviceInfoRequestVo;
import com.lguplus.fleta.service.push.DeviceInfoService;
import com.lguplus.fleta.validation.Groups;
import com.lguplus.fleta.validation.Groups.C1;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
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
@RequestMapping(value = "/v1/push/deviceinfo")
public class DeviceInfoController {

    private final DeviceInfoPostRequestMapper deviceInfoPostRequestMapper;
    private final DeviceInfoService deviceInfoService;

    /**
     * 단말 정보 등록
     *
     * @param deviceInfoRequestVo deviceinfo 등록,수정, 삭제를 위한 VO
     * @return 등록 결과 응답
     */
    @ApiOperation(value="deviceinfo 등록", notes="deviceinfo를 등록한다.")
    @PostMapping
    public SuccessResponseDto postDeviceInfo(
        @Validated(Groups.C1.class) DeviceInfoRequestVo deviceInfoRequestVo){
        DeviceInfoRequestDto deviceInfoRequestDto = deviceInfoPostRequestMapper.toDto(deviceInfoRequestVo);
        deviceInfoService.createDeviceInfo(deviceInfoRequestDto);
        return SuccessResponseDto.builder().build();
    }

    /**
     * 단말 정보 수정(noti_type만 수정)
     *
     * @param deviceInfoRequestVo deviceinfo 등록,수정, 삭제를 위한 VO
     * @return 수정 결과 응답
     */
    @ApiOperation(value="deviceinfo 수정", notes="deviceinfo를 수정한다.")
    @PutMapping
    public SuccessResponseDto putDeviceInfo(
        @Validated(Groups.C1.class) DeviceInfoRequestVo deviceInfoRequestVo){
        DeviceInfoRequestDto deviceInfoRequestDto = deviceInfoPostRequestMapper.toDto(deviceInfoRequestVo);
        deviceInfoService.updateDeviceInfo(deviceInfoRequestDto);
        return SuccessResponseDto.builder().build();
    }

    /**
     * 단말 정보 삭제
     *
     * @param deviceInfoRequestVo deviceinfo 등록,수정, 삭제를 위한 VO
     * @return 삭제 결과 응답
     */
    @ApiOperation(value="deviceinfo 삭제", notes="deviceinfo를 삭제한다.")
    @DeleteMapping
    public SuccessResponseDto deleteDeviceInfo(
        @Validated(Groups.C2.class) DeviceInfoRequestVo deviceInfoRequestVo){
        DeviceInfoRequestDto deviceInfoRequestDto = deviceInfoPostRequestMapper.toDto(deviceInfoRequestVo);
        deviceInfoService.deleteDeviceInfo(deviceInfoRequestDto);
        return SuccessResponseDto.builder().build();
    }
}
