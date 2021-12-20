package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.vo.SendSmsVo;
import org.mapstruct.Mapper;

@Mapper(config = ObjectMapperConfig.class)
public interface SendSmsMapper {
    SendSmsRequestDto toDto(SendSmsVo sendSmsVo);
}
