package com.lguplus.fleta.config.converter;

import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public class PlainTextResponseMessageConverter
        extends AbstractHttpMessageConverter<CommonResponseDto> {

    /**
     *
     */
    public PlainTextResponseMessageConverter() {

        super(MediaType.TEXT_PLAIN);
    }

    /**
     *
     */
    @Override
    public boolean canRead(final Class<?> clazz, final MediaType mediaType) {

        return false;
    }

    /**
     *
     */
    @Override
    protected boolean supports(final Class<?> clazz) {

        return CommonResponseDto.class.isAssignableFrom(clazz);
    }

    /**
     *
     */
    @Override
    protected CommonResponseDto readInternal(final Class<? extends CommonResponseDto> clazz,
                                          final HttpInputMessage inputMessage) {

        throw new UnsupportedOperationException();
    }

    /**
     *
     */
    @Override
    protected void writeInternal(final CommonResponseDto response,
                                 final HttpOutputMessage outputMessage) throws IOException {

        StreamUtils.copy(response.toPlainText(), StandardCharsets.UTF_8, outputMessage.getBody());
    }
}
