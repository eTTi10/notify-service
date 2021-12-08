package com.lguplus.fleta.data.mapper;

import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto;
import com.lguplus.fleta.data.dto.request.SendSmsCodeRequestDto.SendSmsCodeRequestDtoBuilder;
import com.lguplus.fleta.data.vo.SendSmsCodeVo;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-12-08T14:13:29+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.13 (Amazon.com Inc.)"
)
@Component
public class SendSmsCodeMapperImpl implements SendSmsCodeMapper {

    @Override
    public SendSmsCodeRequestDto toDto(SendSmsCodeVo sendSmsCodeVo) {
        if ( sendSmsCodeVo == null ) {
            return null;
        }

        SendSmsCodeRequestDtoBuilder<?, ?> sendSmsCodeRequestDto = SendSmsCodeRequestDto.builder();

        sendSmsCodeRequestDto.saId( sendSmsCodeVo.getSaId() );
        sendSmsCodeRequestDto.stbMac( sendSmsCodeVo.getStbMac() );
        sendSmsCodeRequestDto.smsCd( sendSmsCodeVo.getSmsCd() );
        sendSmsCodeRequestDto.ctn( sendSmsCodeVo.getCtn() );
        sendSmsCodeRequestDto.replacement( sendSmsCodeVo.getReplacement() );

        return sendSmsCodeRequestDto.build();
    }
}
