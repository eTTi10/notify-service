package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto.LatestRequestDtoBuilder;
import com.lguplus.fleta.data.vo.LatestSearchRequestVo;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-09T11:35:40+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.13 (Amazon.com Inc.)"
)
@Component
public class LatestSearchRequestMapperImpl implements LatestSearchRequestMapper {

    @Override
    public LatestRequestDto toDto(LatestSearchRequestVo member) {
        if ( member == null ) {
            return null;
        }

        LatestRequestDtoBuilder<?, ?> latestRequestDto = LatestRequestDto.builder();

        latestRequestDto.saId( member.getSaId() );
        latestRequestDto.mac( member.getMac() );
        latestRequestDto.ctn( member.getCtn() );
        latestRequestDto.catId( member.getCatId() );

        return latestRequestDto.build();
    }
}
