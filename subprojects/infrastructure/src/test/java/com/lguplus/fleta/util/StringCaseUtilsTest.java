package com.lguplus.fleta.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * StringCaseUtils 유닛 테스트
 *
 * @version 1.0
 */
class StringCaseUtilsTest {

    @Test
    void constructor() throws NoSuchMethodException {
        // Given
        Constructor<StringCaseUtils> constructor = StringCaseUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        // When
        ThrowableAssert.ThrowingCallable callable = constructor::newInstance;
        // Then
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        assertThatThrownBy(callable).isInstanceOf(InvocationTargetException.class);
    }

    @ParameterizedTest
    @CsvSource({
        "aBc_DeF, AbcDef",
        "aBc_DeF_ghI, AbcDefGhi",
        "aBcDeF, ABcDeF",
        "ABCDEF, Abcdef",
        "abcdef, Abcdef",
    })
    void autoPascalCase(String given, String expected) {
        // When
        String actual = StringCaseUtils.autoPascalCase(given);
        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void camelCaseToSnakeCase_lowerCase() {
        // Given
        String str = "AbcDef";
        // When
        String actual = StringCaseUtils.toLowerCamelCaseToSnakeCase(str);
        // Then
        assertThat(actual).isEqualTo("abc_def");
    }

    @ParameterizedTest
    @CsvSource({
        "AbcDef, ABC_DEF",
        "aBc_DeF_ghI, A_BC_DE_F_GH_I",
    })
    void camelCaseToSnakeCase(String given, String expected) {
        // When
        String actual = StringCaseUtils.camelCaseToSnakeCase(given);
        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
        "ABC_DEF, AbcDef",
        "A_BC_DE_F_GH_I, ABcDeFGhI",
    })
    void snakeCaseToCamelCase(String given, String expected) {
        // When
        String actual = StringCaseUtils.snakeCaseToCamelCase(given);
        // Then
        assertThat(actual).isEqualTo(expected);
    }
}
