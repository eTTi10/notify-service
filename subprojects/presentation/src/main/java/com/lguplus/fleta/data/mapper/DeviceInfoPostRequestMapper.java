package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.outer.DeviceInfoRequestDto;
import com.lguplus.fleta.data.vo.DeviceInfoPostRequestVo;
import org.mapstruct.Mapper;

@Mapper(config = ObjectMapperConfig.class)
public interface DeviceInfoPostRequestMapper {
    DeviceInfoRequestDto toDto(DeviceInfoPostRequestVo deviceInfoPostRequestVo);
}
