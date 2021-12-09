package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto.HttpPushSingleRequestDtoBuilder;
import com.lguplus.fleta.data.vo.HttpPushSingleRequestVo;
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
public class HttpPushSingleMapperImpl implements HttpPushSingleMapper {

    @Override
    public HttpPushSingleRequestDto toDto(HttpPushSingleRequestVo httpPushSingleRequestVo) {
        if ( httpPushSingleRequestVo == null ) {
            return null;
        }

        HttpPushSingleRequestDtoBuilder<?, ?> httpPushSingleRequestDto = HttpPushSingleRequestDto.builder();

        httpPushSingleRequestDto.appId( httpPushSingleRequestVo.getAppId() );
        httpPushSingleRequestDto.serviceId( httpPushSingleRequestVo.getServiceId() );
        httpPushSingleRequestDto.msg( httpPushSingleRequestVo.getMsg() );
        List<String> list = httpPushSingleRequestVo.getItems();
        if ( list != null ) {
            httpPushSingleRequestDto.items( new ArrayList<String>( list ) );
        }
        List<String> list1 = httpPushSingleRequestVo.getUsers();
        if ( list1 != null ) {
            httpPushSingleRequestDto.users( new ArrayList<String>( list1 ) );
        }

        httpPushSingleRequestDto.pushType( httpPushSingleRequestVo.getPushType().toUpperCase() );

        return httpPushSingleRequestDto.build();
    }
}
