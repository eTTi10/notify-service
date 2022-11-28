package com.lguplus.fleta.repository.push;

import com.lguplus.fleta.data.dto.request.inner.HttpPushRequestDto;
import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.dto.response.inner.DeviceInfosResponseDto;
import com.lguplus.fleta.data.entity.DeviceInfo;

import java.util.List;

public interface DeviceInfoRepository {
    DeviceInfo save(DeviceInfoRequestDto deviceInfoRequestDto);
    void delete(DeviceInfoRequestDto deviceInfoRequestDto);
    boolean exist(DeviceInfoRequestDto deviceInfoRequestDto);
    List<DeviceInfosResponseDto> getDeviceInfos(HttpPushRequestDto deviceInfosRequestDto);
}

