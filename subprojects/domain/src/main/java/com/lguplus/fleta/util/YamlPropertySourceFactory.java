package com.lguplus.fleta.util;

import java.util.Optional;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    final String a = "\"result\":{\"noti_type\":\"FUP\",\"cont_type\":\"REAL\",\"svc_id\":\"[+svc_id]\",\"name\":\"[+name]\",\"service_type\":\"[+service_type]\",\"ctn\":\"[+ctn]\",\"data\":{\"LINK_FLAG\":\"[+link_flag]\", \"intent_url\":\"[+intent_url]\"}}";

    /**
     *
     */
    @Override
    public PropertySource<?> createPropertySource(final String name,
        final EncodedResource resource) {

        final YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        final Resource yaml = resource.getResource();
        factory.setResources(yaml);

        final Properties properties = factory.getObject();
        final String sourceName = name == null ? yaml.getFilename() : name;
        return new PropertiesPropertySource(sourceName == null ? "" : sourceName,
            Optional.of(properties).get());
    }
}
