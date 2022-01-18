package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.inner.HttpPushAnnounceRequestDto;
import com.lguplus.fleta.data.vo.HttpPushAnnounceRequestVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 공지푸시등록 요청 MapStruct Mapper
 *
 */
@Mapper(config = ObjectMapperConfig.class)
public interface HttpPushAnnounceMapper {

    @Mapping(target = "pushType", expression = "java(httpPushAnnounceRequestVo.getPushType().toUpperCase())")
    HttpPushAnnounceRequestDto toDto(HttpPushAnnounceRequestVo httpPushAnnounceRequestVo);

}
