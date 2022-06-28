package com.lguplus.fleta.data.type.converter;

import com.lguplus.fleta.data.type.CarrierType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
@Component
public class CarrierTypeConverter implements Converter<String, CarrierType> {

    /**
     *
     */
    @Override
    public CarrierType convert(final String source) {

        return CarrierType.asValue(source);
    }
}
