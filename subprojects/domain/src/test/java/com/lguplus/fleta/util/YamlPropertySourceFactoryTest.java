package com.lguplus.fleta.util;

import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class YamlPropertySourceFactoryTest {

    @Test
    void test_createPropertySource_Name_Null() {

        Resource resource = new ByteArrayResource("key: value".getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "test.yml";
            }

        };
        EncodedResource encodedResource = new EncodedResource(resource, StandardCharsets.UTF_8);
        YamlPropertySourceFactory factory = new YamlPropertySourceFactory();
        PropertySource<?> propertySource = factory.createPropertySource("test", encodedResource);
        assertThat(propertySource.getProperty("key")).isEqualTo("value");
    }

    @Test
    void test_createPropertySource_FileName_Null() {

        Resource resource = new ByteArrayResource("key: value".getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return null;
            }

        };
        EncodedResource encodedResource = new EncodedResource(resource, StandardCharsets.UTF_8);
        YamlPropertySourceFactory factory = new YamlPropertySourceFactory();
        assertThrows(IllegalArgumentException.class, () -> factory.createPropertySource(null, encodedResource));
    }
}