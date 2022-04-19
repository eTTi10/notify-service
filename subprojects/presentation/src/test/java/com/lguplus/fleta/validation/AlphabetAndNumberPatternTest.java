package com.lguplus.fleta.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lguplus.fleta.validation.AlphabetAndNumberPattern.Validator;

class AlphabetAndNumberPatternTest {

    Validator validator;
            
    @BeforeEach
    void setUp() throws Exception {
        validator = new AlphabetAndNumberPattern.Validator();
    }

    @Test
    void test() {
        boolean result = validator.isValid("a1", null);
        assertThat(result).isTrue();
    }

}
