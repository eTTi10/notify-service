package com.lguplus.fleta.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.lguplus.fleta.validation.Perl5Pattern.Validator;
import static org.assertj.core.api.Assertions.assertThat;

class Perl5PatternTest {

    @Perl5Pattern(regexp = "^\\w+$")
    String dummy;

    Validator validator;

    @BeforeEach
    void setUp() throws Exception {
        Perl5Pattern pattern = this.getClass().getDeclaredField("dummy").getAnnotation(Perl5Pattern.class);
        validator = new Validator();
        validator.initialize(pattern);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "NIL",
            "abcdEFGH1234",
            "abcd한글1234"
    }, nullValues = "NIL")
    void test_WordOrNull(String value) {
        boolean result = validator.isValid(value, null);
        assertThat(result).isTrue();
    }

    @Test
    void test_NotWord() {
        boolean result = validator.isValid("abcdEF*H1234", null);
        assertThat(result).isFalse();
    }
}
