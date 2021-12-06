package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.vo.LatestPostRequestVo;
import org.mapstruct.Mapper;

@Mapper(config = ObjectMapperConfig.class)
public interface LatestPostRequestMapper {
    LatestRequestDto toDto(LatestPostRequestVo member);
}
