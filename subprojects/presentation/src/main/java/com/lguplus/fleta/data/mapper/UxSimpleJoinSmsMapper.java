package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.outer.UxSimpleJoinSmsRequestDto;
import com.lguplus.fleta.data.vo.UxSimpleJoinSmsRequestVo;
import org.mapstruct.Mapper;

/**
 * tvG 유플릭스 간편 가입 안내 SMS 요청 MapStruct Mapper
 *
 */
@Mapper(config = ObjectMapperConfig.class)
public interface UxSimpleJoinSmsMapper {

    UxSimpleJoinSmsRequestDto toDto(UxSimpleJoinSmsRequestVo uxSimpleJoinSmsRequestVo);

}
