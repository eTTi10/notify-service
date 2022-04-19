package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.vo.LatestSearchRequestVo;
import org.mapstruct.Mapper;

@Mapper(config = ObjectMapperConfig.class)
public interface LatestSearchRequestMapper {
    LatestRequestDto toDto(LatestSearchRequestVo member);
}
