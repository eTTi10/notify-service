package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.inner.HttpPushMultiRequestDto;
import com.lguplus.fleta.data.vo.HttpPushMultiRequestVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 멀티푸시등록 요청 MapStruct Mapper
 *
 */
@Mapper(config = ObjectMapperConfig.class)
public interface HttpPushMultiMapper {

    @Mapping(target = "pushType", expression = "java(httpPushMultiRequestVo.getPushType().toUpperCase())")
    HttpPushMultiRequestDto toDto(HttpPushMultiRequestVo httpPushMultiRequestVo);

}
