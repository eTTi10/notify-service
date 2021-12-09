package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.data.dto.request.SendSmsRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsRequestDto.SendSmsRequestDtoBuilder;
import com.lguplus.fleta.data.vo.SendSmsVo;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-09T11:35:40+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.13 (Amazon.com Inc.)"
)
@Component
public class SendSmsMapperImpl implements SendSmsMapper {

    @Override
    public SendSmsRequestDto toDto(SendSmsVo sendSmsVo) {
        if ( sendSmsVo == null ) {
            return null;
        }

        SendSmsRequestDtoBuilder<?, ?> sendSmsRequestDto = SendSmsRequestDto.builder();

        sendSmsRequestDto.msg( sendSmsVo.getMsg() );

        return sendSmsRequestDto.build();
    }
}
