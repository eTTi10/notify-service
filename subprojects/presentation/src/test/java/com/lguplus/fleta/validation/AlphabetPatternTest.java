package com.lguplus.fleta.validation;

import com.lguplus.fleta.validation.AlphabetPattern.Validator;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlphabetPatternTest {

    Validator validator;

    @BeforeEach
    void setUp() throws Exception {
        validator = new AlphabetPattern.Validator();
    }

    @Test
    void testIsValid_1() {
        boolean result = validator.isValid("a", null);
        assertThat(result).isTrue();
    }

}
