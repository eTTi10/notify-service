package com.lguplus.fleta.util;

import com.lguplus.fleta.testutil.TestDto;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * VariableCastUtils 유닛 테스트
 * @version 1.0
 */
class VariableCastUtilsTest {

    @Test
    void constructor() throws NoSuchMethodException {
        // Given
        Constructor<VariableCastUtils> constructor = VariableCastUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        // When
        ThrowableAssert.ThrowingCallable callable = constructor::newInstance;
        // Then
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        assertThatThrownBy(callable).isInstanceOf(InvocationTargetException.class);
    }

    @Test
    void castValue_null() {
        // Given
        Object testParam = null;
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, String.class);
        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void castValue_primitive() {
        // Given
        Object testParam = 10;
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, int.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(testParam);
            assertThat(a).isInstanceOf(Integer.class);
        });
    }

    @Test
    void castValue_string() {
        // Given
        Object testParam = "Megazone";
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, String.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(testParam);
            assertThat(a).isInstanceOf(String.class);
        });
    }

    @Test
    void castValue_boolean() {
        // Given
        Object testParam = true;
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Boolean.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(testParam);
            assertThat(a).isInstanceOf(Boolean.class);
        });
    }

    @Test
    void castValue_booleanOfNumber() {
        // Given
        Object testParam = "1";
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Boolean.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(true);
            assertThat(a).isInstanceOf(Boolean.class);
        });
    }

    @Test
    void castValue_int() {
        // Given
        Object testParam = 10;
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Integer.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(testParam);
            assertThat(a).isInstanceOf(Integer.class);
        });
    }

    @Test
    void castValue_intOfString() {
        // Given
        Object testParam = "10";
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Integer.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(10);
            assertThat(a).isInstanceOf(Integer.class);
        });
    }

    @Test
    void castValue_intOfOtherObject() {
        // Given
        Object testParam = new TestDto(); // Invalid Type
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Integer.class);
        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void castValue_long() {
        // Given
        Object testParam = 10L;
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Long.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(testParam);
            assertThat(a).isInstanceOf(Long.class);
        });
    }

    @Test
    void castValue_longOfString() {
        // Given
        Object testParam = "10";
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Long.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(10L);
            assertThat(a).isInstanceOf(Long.class);
        });
    }

    @Test
    void castValue_longOfOtherObject() {
        // Given
        Object testParam = new TestDto(); // Invalid Type
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Long.class);
        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void castValue_float() {
        // Given
        Object testParam = 12.3;
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Float.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(12.3F);
            assertThat(a).isInstanceOf(Float.class);
        });
    }

    @Test
    void castValue_floatOfString() {
        // Given
        Object testParam = "12.3";
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Float.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(12.3F);
            assertThat(a).isInstanceOf(Float.class);
        });
    }

    @Test
    void castValue_floatOfOtherObject() {
        // Given
        TestDto testParam = new TestDto();
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Float.class);
        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void castValue_double() {
        // Given
        Object testParam = 12.3;
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Double.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(12.3D);
            assertThat(a).isInstanceOf(Double.class);
        });
    }

    @Test
    void castValue_doubleOfString() {
        // Given
        Object testParam = "12.3";
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Double.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(12.3D);
            assertThat(a).isInstanceOf(Double.class);
        });
    }

    @Test
    void castValue_douleOfOtherObject() {
        // Given
        TestDto testParam = new TestDto();
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, Double.class);
        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void castValue_localDate() {
        // Given
        Object testParam = LocalDate.of(2022, 1, 26);
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, LocalDate.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(testParam);
            assertThat(a).isInstanceOf(LocalDate.class);
        });
    }

    @Test
    void castValue_localDateOfTimestamp() {
        // Given
        LocalDate localDate = LocalDate.of(2022, 1, 26);
        Object testParam = Timestamp.valueOf("2022-01-26 20:46:07");
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, LocalDate.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(localDate);
            assertThat(a).isInstanceOf(LocalDate.class);
        });
    }

    @Test
    void castValue_localDateOfOtherObject() {
        // Given
        TestDto testParam = new TestDto();
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, LocalDate.class);
        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void castValue_localDateTime() {
        // Given
        Object testParam = LocalDateTime.of(2022, 1, 26, 20, 46, 7);
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, LocalDateTime.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(testParam);
            assertThat(a).isInstanceOf(LocalDateTime.class);
        });
    }

    @Test
    void castValue_localDateTimeOfTimestamp() {
        // Given
        LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 26, 20, 46, 7);
        Object testParam = Timestamp.valueOf("2022-01-26 20:46:07");
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, LocalDateTime.class);
        // Then
        assertThat(actual).isPresent();
        actual.ifPresent(a -> {
            assertThat(a).isEqualTo(localDateTime);
            assertThat(a).isInstanceOf(LocalDateTime.class);
        });
    }

    @Test
    void castValue_localDateTimeOfOtherObject() {
        // Given
        TestDto testParam = new TestDto();
        // When
        Optional<Object> actual = VariableCastUtils.castValue(testParam, LocalDateTime.class);
        // Then
        assertThat(actual).isEmpty();
    }
}
