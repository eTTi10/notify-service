package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.vo.HttpPushSingleRequestVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 단건푸시등록 요청 MapStruct Mapper
 */
@Mapper(config = ObjectMapperConfig.class)
public interface HttpPushSingleMapper {

    @Mapping(target = "pushType", expression = "java(httpPushSingleRequestVo.getPushType().toUpperCase())")
    HttpPushSingleRequestDto toDto(HttpPushSingleRequestVo httpPushSingleRequestVo);

}
