package com.lguplus.fleta.api.outer.musicshow;

import com.lguplus.fleta.service.musicshow.MusicShowService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(MusicshowController.class)
class MusicshowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MusicShowService service;

        @Test
        @DisplayName("정상 조회 테스트")
        void getPush() {
        }

//        @Test
//        void registerPush() {
//        }
//
//        @Test
//        void deletePush() {
//        }
}