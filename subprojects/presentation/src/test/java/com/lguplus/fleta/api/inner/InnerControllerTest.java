package com.lguplus.fleta.api.inner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class InnerControllerTest {
    
    @InjectMocks
    private InnerController innerController;
    
    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    void testSendMessage() {

    }

}