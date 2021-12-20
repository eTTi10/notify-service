package com.lguplus.fleta.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.lguplus.fleta.validation.NetworkTypePattern.Validator;

class NetworkTypePatternTest {

    Validator validator;
    
    @BeforeEach
    void setUp() throws Exception {
        new Groups();
        
        validator = new NetworkTypePattern.Validator();
    }

    @Test
    void testIsValid_1() {
        boolean result = validator.isValid("1", null);
        assertThat(result).isEqualTo(true);
    }
    
    @Test
    void testIsValid_2() {
        boolean result = validator.isValid(null, null);
        assertThat(result).isEqualTo(true);
    }
    
    @Test
    void testIsValid_3() {
        boolean result = validator.isValid("NONE", null);
        assertThat(result).isEqualTo(false);
    }

}
