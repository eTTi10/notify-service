package com.lguplus.fleta.config;

import com.lguplus.fleta.config.binder.ParamAliasProcessor;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
@Configuration
public class ArgumentResolverConfig implements WebMvcConfigurer {

    /**
     *
     */
    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {

        argumentResolvers.add(new ParamAliasProcessor());
    }
}
