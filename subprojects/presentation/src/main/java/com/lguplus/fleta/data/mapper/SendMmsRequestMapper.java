package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.vo.SendMmsVo;
import org.mapstruct.Mapper;

@Mapper(config = ObjectMapperConfig.class)
public interface SendMmsRequestMapper {
    SendMmsRequestDto toDto(SendMmsVo member);
}
