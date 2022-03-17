package com.lguplus.fleta.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator.Feature;
import com.lguplus.fleta.config.converter.PlainTextResponseMessageConverter;
import com.lguplus.fleta.data.dto.response.CommonErrorResponseDto;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.data.dto.response.RootErrorResponseDto;
import com.lguplus.fleta.data.dto.response.RootResponseDto;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
@Configuration
public class MessageConverterConfig implements WebMvcConfigurer {

    /**
     *
     */
    @Bean
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {

        final ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new MappingJackson2HttpMessageConverter(objectMapper) {

            @Override
            protected void writeInternal(final Object object, final Type type,
                                         final HttpOutputMessage outputMessage)
                    throws IOException, HttpMessageNotWritableException {

                if (object instanceof CommonErrorResponseDto) {
                    final Object wrappedObject = RootErrorResponseDto.builder()
                            .error((CommonErrorResponseDto)object)
                            .build();
                    super.writeInternal(wrappedObject, wrappedObject.getClass(), outputMessage);
                }
                else if (object instanceof CommonResponseDto) {
                    final Object wrappedObject = RootResponseDto.builder()
                            .result((CommonResponseDto)object)
                            .build();
                    super.writeInternal(wrappedObject, wrappedObject.getClass(), outputMessage);
                }
                else {
                    super.writeInternal(object, type, outputMessage);
                }
            }

            @Override
            public Object read(final Type type, final  Class<?> contextClass, final HttpInputMessage inputMessage)
                    throws IOException, HttpMessageNotReadableException {

                if (TypeUtils.isAssignable(type, CommonErrorResponseDto.class)) {
                    final Type wrappedType = TypeUtils.parameterize(RootErrorResponseDto.class, type);
                    return ((RootErrorResponseDto<?>)super.read(wrappedType, contextClass, inputMessage)).getError();
                }
                else if (TypeUtils.isAssignable(type, CommonResponseDto.class)) {
                    final Type wrappedType = TypeUtils.parameterize(RootResponseDto.class, type);
                    return ((RootResponseDto<?>)super.read(wrappedType, contextClass, inputMessage)).getResult();
                }
                else {
                    return super.read(type, contextClass, inputMessage);
                }
            }
        };
    }

    /**
     *
     */
    @Bean
    MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter() {

        final XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(Feature.WRITE_XML_DECLARATION, true);
        return new MappingJackson2XmlHttpMessageConverter(xmlMapper);
    }

    /**
     *
     */
    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {

        converters.add(new PlainTextResponseMessageConverter());
    }

    /**
     *
     */
    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {

        configurer.defaultContentType(MediaType.TEXT_PLAIN);
    }
}
