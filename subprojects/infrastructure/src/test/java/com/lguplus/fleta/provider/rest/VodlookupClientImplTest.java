package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.data.dto.AlbumProgrammingDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class VodlookupClientImplTest {

    @InjectMocks
    VodlookupClientImpl vodlookupClient;

    @Mock
    VodlookupFeignClient vodlookupFeignClient;

    @Test
    void getAlbumProgramming() {

        AlbumProgrammingDto resultDto = AlbumProgrammingDto.builder()
            .albumId("M010710P04PPV00")
            .build();

        InnerResponseDto<List<AlbumProgrammingDto>> resultList = new InnerResponseDto<>(InnerResponseCodeType.OK, List.of(resultDto));
        given(vodlookupFeignClient.getAlbumProgramming(any(), any())).willReturn(resultList);

        List<AlbumProgrammingDto> list = vodlookupClient.getAlbumProgramming("NSC", List.of("M010710P04PPV00"));

        assertThat(list.size()).isEqualTo(1);
    }
}