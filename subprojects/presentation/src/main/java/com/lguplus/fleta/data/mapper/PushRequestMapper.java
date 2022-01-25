package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.config.ObjectMapperConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.vo.PushRequestBodyAnnounceVo;
import com.lguplus.fleta.data.vo.PushRequestBodyMultiVo;
import com.lguplus.fleta.data.vo.PushRequestBodySingleVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 푸시등록 요청 MapStruct Mapper
 *
 */
@Mapper(config = ObjectMapperConfig.class)
public interface PushRequestMapper {

    @Mapping(target = "items", expression = "java(getItems(vo.getAddItems()))")
    PushRequestAnnounceDto toDtoAnnounce(PushRequestBodyAnnounceVo vo);

    @Mapping(target = "items", expression = "java(getItems(vo.getAddItems()))")
    PushRequestSingleDto toDtoSingle(PushRequestBodySingleVo vo);

    @Mapping(target = "items", expression = "java(getItems(vo.getAddItems()))")
    PushRequestMultiDto toDtoMulti(PushRequestBodyMultiVo vo);

    default List<PushRequestItemDto> getItems(List<String> addItems) {
        List<PushRequestItemDto> items = new ArrayList<>();
        addItems.forEach(e -> {
            String[] parseItems = e.split("!\\^");
            if (parseItems.length == 2) {
                items.add(PushRequestItemDto.builder().itemKey(parseItems[0]).itemValue(parseItems[1]).build());
            }
        });
        return items;
    }

}
