package com.lguplus.fleta.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lguplus.fleta.validation.AlphabetOrEmptyPattern.Validator;

class AlphabetOrEmptyPatternTest {

    Validator validator;
    
    @BeforeEach
    void setUp() throws Exception {
        validator = new AlphabetOrEmptyPattern.Validator();
    }

    @Test
    void test() {
        boolean result = validator.isValid("ab", null);
        assertThat(result).isTrue();
    }
    
    @Test
    void test_2() {
        boolean result = validator.isValid("", null);
        assertThat(result).isTrue();
    }
}
