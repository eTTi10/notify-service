package com.lguplus.fleta.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lguplus.fleta.validation.NumberPattern.Validator;

class NumberPatternTest {

    Validator validator; 
    
    @BeforeEach
    void setUp() throws Exception {
        validator = new NumberPattern.Validator();
    }

    @Test
    void test() {
        boolean result = validator.isValid("1", null);
        assertThat(result).isEqualTo(true);
    }

}
