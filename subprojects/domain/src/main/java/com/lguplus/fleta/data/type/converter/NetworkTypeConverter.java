package com.lguplus.fleta.data.type.converter;

import com.lguplus.fleta.data.type.NetworkType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
@Component
public class NetworkTypeConverter implements Converter<String, NetworkType> {

    /**
     *
     */
    @Override
    public NetworkType convert(final String source) {

        return NetworkType.asValue(source);
    }
}
