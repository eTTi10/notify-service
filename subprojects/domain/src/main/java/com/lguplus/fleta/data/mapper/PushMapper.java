package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(config = ObjectMapperConfig.class)
public interface PushMapper {

   @Mapping(expression = "java(map.get(\"msg_id\"))", target = "msgId")
   @Mapping(expression = "java(map.get(\"push_id\"))", target = "pushId")
   @Mapping(expression = "java(map.get(\"status_code\"))", target = "statusCode")
   @Mapping(expression = "java(map.get(\"status_msg\"))", target = "statusMsg")
   PushResponseDto toResponseDto(final Map<String, String> map);

}
