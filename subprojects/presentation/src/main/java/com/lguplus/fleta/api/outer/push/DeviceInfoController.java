package com.lguplus.fleta.api.outer.push;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.mapper.DeviceInfoPostRequestMapper;
import com.lguplus.fleta.data.vo.DeviceInfoRequestVo;
import com.lguplus.fleta.service.push.DeviceInfoService;
import com.lguplus.fleta.validation.Groups;
import com.lguplus.fleta.validation.Groups.C1;
import io.swagger.annotations.Api;
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

@Api(tags = "PUSH 발송을위한단말정보등록")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/push/deviceinfo")
public class DeviceInfoController {

    private final DeviceInfoPostRequestMapper deviceInfoPostRequestMapper;
    private final DeviceInfoService deviceInfoService;

    @PostMapping
    public SuccessResponseDto postDeviceInfo(
        @Validated(Groups.C1.class) DeviceInfoRequestVo deviceInfoRequestVo){
        DeviceInfoRequestDto deviceInfoRequestDto = deviceInfoPostRequestMapper.toDto(deviceInfoRequestVo);
        deviceInfoService.createDeviceInfo(deviceInfoRequestDto);
        return SuccessResponseDto.builder().build();
    }

    @PutMapping
    public SuccessResponseDto putDeviceInfo(
        @Validated(Groups.C1.class) DeviceInfoRequestVo deviceInfoRequestVo){
        DeviceInfoRequestDto deviceInfoRequestDto = deviceInfoPostRequestMapper.toDto(deviceInfoRequestVo);
        deviceInfoService.updateDeviceInfo(deviceInfoRequestDto);
        return SuccessResponseDto.builder().build();
    }

    @DeleteMapping
    public SuccessResponseDto deleteDeviceInfo(
        @Validated(Groups.C2.class) DeviceInfoRequestVo deviceInfoRequestVo){
        DeviceInfoRequestDto deviceInfoRequestDto = deviceInfoPostRequestMapper.toDto(deviceInfoRequestVo);
        deviceInfoService.deleteDeviceInfo(deviceInfoRequestDto);
        return SuccessResponseDto.builder().build();
    }
}
