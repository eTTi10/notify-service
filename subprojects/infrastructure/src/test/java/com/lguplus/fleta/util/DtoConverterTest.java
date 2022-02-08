package com.lguplus.fleta.util;

import com.lguplus.fleta.exception.ServiceException;
import com.lguplus.fleta.testutil.TestDto;
import com.lguplus.fleta.testutil.TestErrorDto;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * DtoConverter 유닛 테스트
 * @version 1.0
 */
class DtoConverterTest {

    TestDto expected;

    @BeforeEach
    void beforeEach() {
        expected = new TestDto(1, "Megazone");
    }

    @Test
    void constructor() throws NoSuchMethodException {
        // Given
        Constructor<DtoConverter> constructor = DtoConverter.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        // When
        ThrowableAssert.ThrowingCallable callable = constructor::newInstance;
        // Then
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        assertThatThrownBy(callable).isInstanceOf(InvocationTargetException.class);
    }

    @Test
    void convertList() {
        // Given
        List<Map<String, Object>> expectedListMap = new ArrayList<>();
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("id", expected.getId());
        expectedMap.put("name", expected.getName());
        expectedListMap.add(expectedMap);
        expectedListMap.add(expectedMap);
        // When
        List<TestDto> actual = DtoConverter.convertList(expectedListMap, TestDto.class);
        // Then
        assertThat(actual.size()).isEqualTo(expectedListMap.size());
        assertThat(actual.get(0).getId()).isEqualTo(expected.getId());
    }

    @Test
    void convertList_null() {
        // When
        List<TestDto> actual = DtoConverter.convertList(null, TestDto.class);
        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void convertList_emptyList() {
        // Given
        List<Map<String, Object>> emptyList = new ArrayList<>();
        // When
        List<TestDto> actual = DtoConverter.convertList(emptyList, TestDto.class);
        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void convertList_constructorOfClassException() {
        // Given
        List<Map<String, Object>> expectedListMap = new ArrayList<>();
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("id", expected.getId());
        expectedMap.put("name", expected.getName());
        expectedListMap.add(expectedMap);
        // When
        ThrowableAssert.ThrowingCallable callable = () -> DtoConverter.convertList(expectedListMap, TestErrorDto.class);
        // Then
        assertThatThrownBy(callable).isInstanceOf(ServiceException.class);
    }

    @Test
    void convertSingle() {
        // Given
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("id", expected.getId());
        expectedMap.put("name", expected.getName());
        // When
        Optional<TestDto> actual = DtoConverter.convertSingle(expectedMap, TestDto.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> assertThat(a.getId()).isEqualTo(expected.getId()));
    }

    @Test
    void convertSingle_empty() {
        // Given
        Map<String, Object> expectedMap = new HashMap<>();
        // When
        Optional<TestDto> actual = DtoConverter.convertSingle(expectedMap, TestDto.class);
        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void convertSingle_null() {
        // Given
        Object nullObj = null;
        // When
        Optional<TestDto> actual = DtoConverter.convertSingle(nullObj, TestDto.class);
        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void convertSingle_constructorOfClassException() {
        // Given
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("id", expected.getId());
        expectedMap.put("name", expected.getName());
        // When
        ThrowableAssert.ThrowingCallable callable = () -> DtoConverter.convertSingle(expectedMap, TestErrorDto.class);
        // Then
        assertThatThrownBy(callable).isInstanceOf(ServiceException.class);
    }
}
