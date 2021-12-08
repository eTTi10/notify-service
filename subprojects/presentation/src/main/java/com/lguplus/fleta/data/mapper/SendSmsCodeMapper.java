package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.vo.SendSmsCodeVo;
import org.mapstruct.Mapper;

@Mapper(config = ObjectMapperConfig.class)
public interface SendSmsCodeMapper {
    SendSmsCodeRequestDto toDto(SendSmsCodeVo sendSmsCodeVo);
}
