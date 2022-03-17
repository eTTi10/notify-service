package com.lguplus.fleta.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lguplus.fleta.validation.AlphabetAndNumberOrEmptyPattern.Validator;

class AlphabetAndNumberOrEmptyPatternTest {

    Validator validator; 
    
    @BeforeEach
    void setUp() throws Exception {
        validator = new AlphabetAndNumberOrEmptyPattern.Validator();
    }

    @Test
    void test() {
        boolean result = validator.isValid("12", null);
        assertThat(result).isTrue();
    }
    
    @Test
    void test_2() {
        boolean result = validator.isValid("", null);
        assertThat(result).isTrue();
    }

}
