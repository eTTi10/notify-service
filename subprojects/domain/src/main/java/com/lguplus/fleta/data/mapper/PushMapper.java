package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseMultiDto;
import com.lguplus.fleta.data.dto.response.inner.PushMultiResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ObjectMapperConfig.class)
public interface PushMapper {

    @Mapping(expression = "java(map.get(\"msg_id\"))", target = "messageId")
    @Mapping(expression = "java(map.get(\"push_id\"))", target = "pushId")
    @Mapping(expression = "java(map.get(\"status_code\"))", target = "statusCode")
    @Mapping(expression = "java(map.get(\"status_msg\"))", target = "statusMsg")
    PushResponseDto toResponseDto(final Map<String, String> map);

    @Mapping(expression = "java(dto.getStatusCode())", target = "code")
    @Mapping(expression = "java(dto.getStatusMsg())", target = "message")
    PushClientResponseMultiDto toClientResponseDto(final PushMultiResponseDto dto);

}
