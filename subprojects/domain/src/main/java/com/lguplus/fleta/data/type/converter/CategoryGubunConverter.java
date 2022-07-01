package com.lguplus.fleta.data.type.converter;

import com.lguplus.fleta.data.type.CategoryGubun;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
@Component
public class CategoryGubunConverter implements Converter<String, CategoryGubun> {

    /**
     *
     */
    @Override
    public CategoryGubun convert(final String source) {

        return CategoryGubun.asValue(source);
    }
}
