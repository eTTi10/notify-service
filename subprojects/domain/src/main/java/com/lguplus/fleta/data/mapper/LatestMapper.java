package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.entity.Latest;
import org.mapstruct.Mapper;

@Mapper(config = ObjectMapperConfig.class)
public interface LatestMapper {

    LatestDto toDto(Latest member);
}
