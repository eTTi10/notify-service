package com.lguplus.fleta.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lguplus.fleta.validation.NumberOrEmptyPattern.Validator;

class NumberOrEmptyPatternTest {

    Validator validator;
    
    @BeforeEach
    void setUp() throws Exception {
        validator = new NumberOrEmptyPattern.Validator();
    }

    @Test
    void test_1() {
        boolean result = validator.isValid("12", null);
        assertThat(result).isTrue();
    }
    
    @Test
    void test_2() {
        boolean result = validator.isValid("", null);
        assertThat(result).isTrue();
    }

}
