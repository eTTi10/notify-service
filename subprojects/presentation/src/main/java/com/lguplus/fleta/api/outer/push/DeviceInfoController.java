package com.lguplus.fleta.api.outer.push;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.mapper.DeviceInfoPostRequestMapper;
import com.lguplus.fleta.data.vo.DeviceInfoPostRequestVo;
import com.lguplus.fleta.service.push.DeviceInfoService;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "PUSH 발송을위한단말정보등록")
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class DeviceInfoController {

    private final DeviceInfoPostRequestMapper deviceInfoPostRequestMapper;
    private final DeviceInfoService deviceInfoService;

    @PostMapping(value = "/v1/push/deviceinfo")
    public SuccessResponseDto postDeviceInfo(
        @Valid DeviceInfoPostRequestVo deviceInfoPostRequestVo){
        DeviceInfoRequestDto deviceInfoRequestDto = deviceInfoPostRequestMapper.toDto(deviceInfoPostRequestVo);
        deviceInfoService.createDeviceInfo(deviceInfoRequestDto);
        return SuccessResponseDto.builder().build();
    }
}
