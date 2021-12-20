package com.lguplus.fleta.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class YamlPropertySourceFactoryTest {

    @Test
    void test_createPropertySource_Name_Null() {

        Resource resource = new ByteArrayResource("key: value".getBytes(StandardCharsets.UTF_8)) {
            @Override public String getFilename() { return "test.yml"; };
        };
        EncodedResource encodedResource = new EncodedResource(resource, StandardCharsets.UTF_8);
        YamlPropertySourceFactory factory = new YamlPropertySourceFactory();
        PropertySource<?> propertySource = factory.createPropertySource(null, encodedResource);
        assertThat(propertySource.getProperty("key")).isEqualTo("value");
    }
}