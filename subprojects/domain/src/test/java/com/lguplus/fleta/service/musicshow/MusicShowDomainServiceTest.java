package com.lguplus.fleta.service.musicshow;

import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.repository.musicshow.MusicShowRepository;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("MusicShowDomainService 테스트")
class MusicShowDomainServiceTest {

    @InjectMocks
    MusicShowDomainService domainService;

    @Mock
    MusicShowRepository repository;

    @Test
    void getPush() {
        domainService = new MusicShowDomainService(repository);

        GetPushDto dto = GetPushDto.builder()
            .pushYn("Y")
            .albumId("M01198F334PPV00")
            .resultCode("01")
            .startDt("201908161900")
            .build();

        given(repository.getPush(any())).willReturn(dto);

        GetPushRequestDto requestDto = GetPushRequestDto.builder()
            .saId("1000494369")
            .stbMac("v010.0049.4369")
            .albumId("M01198F334PPV00")
            .build();

        GetPushDto resultDto = domainService.getPush(requestDto);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getAlbumId()).isEqualTo(dto.getAlbumId());


    }
}