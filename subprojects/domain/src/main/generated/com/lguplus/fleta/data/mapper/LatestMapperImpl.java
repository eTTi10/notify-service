package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.LatestDto.LatestDtoBuilder;
import com.lguplus.fleta.data.entity.LatestEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-06T17:55:56+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.13 (Amazon.com Inc.)"
)
@Component
public class LatestMapperImpl implements LatestMapper {

    @Override
    public LatestDto toDto(LatestEntity member) {
        if ( member == null ) {
            return null;
        }

        LatestDtoBuilder<?, ?> latestDto = LatestDto.builder();

        latestDto.saId( member.getSaId() );
        latestDto.mac( member.getMac() );
        latestDto.ctn( member.getCtn() );
        latestDto.catId( member.getCatId() );
        latestDto.regId( member.getRegId() );
        latestDto.catName( member.getCatName() );
        latestDto.categoryGb( member.getCategoryGb() );

        return latestDto.build();
    }
}
