package com.lguplus.fleta.data.type.converter;

import com.lguplus.fleta.data.type.PagingType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
@Component
public class PagingTypeConverter implements Converter<String, PagingType> {

    /**
     *
     */
    @Override
    public PagingType convert(final String source) {

        return PagingType.asValue(source);
    }
}
