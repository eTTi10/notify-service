package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.BootConfig;
import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {BootConfig.class})
class MusicShowJpaEmRepositoryTest {

    @Autowired
    MusicShowJpaEmRepository emRepository;

    @Test
    void getPush() {
        GetPushDto dto = GetPushDto.builder()
            .pushYn("Y")
            .albumId("M01198F334PPV00")
            .resultCode("01")
            .startDt("201908161900")
            .build();

        GetPushRequestDto requestDto = GetPushRequestDto.builder()
            .saId("1000494369")
            .stbMac("v010.0049.4369")
            .albumId("M01198F334PPV00")
            .serviceType("C")
            .build();

        GetPushDto resultDto = this.emRepository.getPush(requestDto);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getAlbumId()).isEqualTo(dto.getAlbumId());


    }
}