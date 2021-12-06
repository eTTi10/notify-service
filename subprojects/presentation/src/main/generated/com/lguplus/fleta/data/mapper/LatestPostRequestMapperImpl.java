package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto.LatestRequestDtoBuilder;
import com.lguplus.fleta.data.vo.LatestPostRequestVo;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-06T16:13:37+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.13 (Amazon.com Inc.)"
)
@Component
public class LatestPostRequestMapperImpl implements LatestPostRequestMapper {

    @Override
    public LatestRequestDto toDto(LatestPostRequestVo member) {
        if ( member == null ) {
            return null;
        }

        LatestRequestDtoBuilder<?, ?> latestRequestDto = LatestRequestDto.builder();

        latestRequestDto.saId( member.getSaId() );
        latestRequestDto.mac( member.getMac() );
        latestRequestDto.ctn( member.getCtn() );
        latestRequestDto.catId( member.getCatId() );
        latestRequestDto.regId( member.getRegId() );
        latestRequestDto.catName( member.getCatName() );
        latestRequestDto.categoryGb( member.getCategoryGb() );

        return latestRequestDto.build();
    }
}
