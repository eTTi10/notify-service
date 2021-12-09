package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto.PushRequestAnnounceDtoBuilder;
import com.lguplus.fleta.data.vo.PushRequestBodyAnnounceVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-09T11:35:40+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.13 (Amazon.com Inc.)"
)
@Component
public class PushRequestAnnounceMapperImpl implements PushRequestAnnounceMapper {

    @Override
    public PushRequestAnnounceDto toDto(PushRequestBodyAnnounceVo vo) {
        if ( vo == null ) {
            return null;
        }

        PushRequestAnnounceDtoBuilder<?, ?> pushRequestAnnounceDto = PushRequestAnnounceDto.builder();

        pushRequestAnnounceDto.appId( vo.getAppId() );
        pushRequestAnnounceDto.serviceId( vo.getServiceId() );
        pushRequestAnnounceDto.pushType( vo.getPushType() );
        pushRequestAnnounceDto.msg( vo.getMsg() );
        List<String> list = vo.getItems();
        if ( list != null ) {
            pushRequestAnnounceDto.items( new ArrayList<String>( list ) );
        }

        return pushRequestAnnounceDto.build();
    }
}
