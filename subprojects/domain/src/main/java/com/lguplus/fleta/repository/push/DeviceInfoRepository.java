package com.lguplus.fleta.repository.push;

import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.entity.DeviceInfoEntity;

public interface DeviceInfoRepository {
    DeviceInfoEntity save(DeviceInfoRequestDto deviceInfoRequestDto);
    void delete(DeviceInfoRequestDto deviceInfoRequestDto);
    boolean exist(DeviceInfoRequestDto deviceInfoRequestDto);
}

