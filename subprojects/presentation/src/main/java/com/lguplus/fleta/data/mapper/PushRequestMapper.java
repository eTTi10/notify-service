package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.vo.PushRequestBodyAnnounceVo;
import com.lguplus.fleta.data.vo.PushRequestBodyMultiVo;
import com.lguplus.fleta.data.vo.PushRequestBodySingleVo;
import org.mapstruct.Mapper;

/**
 * 푸시등록 요청 MapStruct Mapper
 *
 */
@Mapper(config = ObjectMapperConfig.class)
public interface PushRequestMapper {

    PushRequestAnnounceDto toDtoAnnounce(PushRequestBodyAnnounceVo vo);

    PushRequestSingleDto toDtoSingle(PushRequestBodySingleVo vo);

    PushRequestMultiDto toDtoMulti(PushRequestBodyMultiVo vo);

}
