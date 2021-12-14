package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.vo.PushRequestBodyAnnounceVo;
import org.mapstruct.Mapper;

/**
 * 푸시등록 요청 MapStruct Mapper
 *
 */
@Mapper(config = ObjectMapperConfig.class)
public interface PushRequestAnnounceMapper {

    PushRequestAnnounceDto toDto(PushRequestBodyAnnounceVo vo);

}
