package com.lguplus.fleta.domain.repository;

import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.provider.jpa.MusicShowJpaEmRepository;
import com.lguplus.fleta.service.musicshow.MusicShowDomainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MusicShowRepositoryImplTest {

    @InjectMocks
    MusicShowRepositoryImpl repositoryImpl;

    @Mock
    MusicShowJpaEmRepository emRepository;

    @Test
    void getPush() {

        GetPushDto dto = GetPushDto.builder()
            .pushYn("Y")
            .albumId("M01198F334PPV00")
            .resultCode("01")
            .startDt("201908161900")
            .build();

        given(emRepository.getPush(any())).willReturn(dto);

        GetPushRequestDto requestDto = GetPushRequestDto.builder()
            .saId("1000494369")
            .stbMac("v010.0049.4369")
            .albumId("M01198F334PPV00")
            .build();

        GetPushDto resultDto = repositoryImpl.getPush(requestDto);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getAlbumId()).isEqualTo(dto.getAlbumId());

    }
}