package com.lguplus.fleta.data.type.converter;

import com.lguplus.fleta.data.type.DeviceInfo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
@Component
public class DeviceInfoConverter implements Converter<String, DeviceInfo> {

    /**
     *
     */
    @Override
    public DeviceInfo convert(final String source) {

        return DeviceInfo.asValue(source);
    }
}
