package com.lguplus.fleta.repository.push;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.entity.DeviceInfo;

public interface DeviceInfoRepository {
    DeviceInfo save(DeviceInfoRequestDto deviceInfoRequestDto);
    void delete(DeviceInfoRequestDto deviceInfoRequestDto);
    boolean exist(DeviceInfoRequestDto deviceInfoRequestDto);
}

