package com.lguplus.fleta.service.musicshow;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import com.lguplus.fleta.data.entity.PushTargetEntity;
import com.lguplus.fleta.exception.push.NotFoundException;
import com.lguplus.fleta.repository.musicshow.MusicShowRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MusicShowDomainService 테스트")
class MusicShowDomainServiceTest {

    @InjectMocks
    MusicShowDomainService domainService;

    @Mock
    MusicShowRepository repository;

    @BeforeEach
    void setup() {
        domainService = new MusicShowDomainService(repository);
    }

    @Test
    void getPush() {

        GetPushDto dto = GetPushDto.builder()
            .pushYn("Y")
            .albumId("M01198F334PPV00")
            .resultCode("01")
            .startDt("201908161900")
            .build();

        given(repository.getPush(any())).willReturn(dto);

        PushRequestDto requestDto = PushRequestDto.builder()
            .saId("1000494369")
            .stbMac("v010.0049.4369")
            .albumId("M01198F334PPV00")
            .build();

        GetPushDto resultDto = domainService.getPush(requestDto);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getAlbumId()).isEqualTo(dto.getAlbumId());

    }

    @Test
    void postPush_inValidAlbumId() {

        given(repository.validAlbumId(any())).willReturn(0);

        PushRequestDto requestDto = PushRequestDto.builder()
            .saId("1000494369")
            .stbMac("v010.0049.4369")
            .albumId("M01198F334PPV00")
            .build();

        assertThrows(NotFoundException.class, () -> domainService.postPush(requestDto));
    }

    @Test
    void postPush_insert() {
        PushTargetEntity resultEntity = PushTargetEntity.builder()
            .saId("1000494369")
            .stbMac("v010.0049.4369")
            .albumId("M01198F334TEST")
            .categoryId("E967O")
            .build();

        given(repository.validAlbumId(any())).willReturn(1);
        given(repository.getPushWithPkey(any())).willReturn(null);
        given(repository.insertPush(any())).willReturn(resultEntity);

        PushRequestDto requestDto = PushRequestDto.builder()
            .saId("1000494369")
            .stbMac("v010.0049.4369")
            .albumId("M01198F334TEST")
            .categoryId("E967O")
            .sendDt("202201211214")
            .build();

        PushTargetEntity entity = domainService.postPush(requestDto);

        assertThat(entity.getAlbumId()).isEqualTo(requestDto.getAlbumId());
        assertThat(entity.getAlbumId()).isEqualTo(requestDto.getAlbumId());
    }

    @Test
    void releasePush() {
        PushRequestDto requestDto = PushRequestDto.builder()
            .saId("500055344423")
            .stbMac("v000.5534.4423")
            .albumId("M0118C3162PPV00")
            .serviceType("C")
            .pushYn("N")
            .build();

        GetPushWithPKeyDto getKeyDto = GetPushWithPKeyDto.builder()
            .pKey(0)
            .saId("500055344423")
            .regNo(11843)
            .stbMac("v000.5534.4423")
            .albumId("M0118C3162PPV00")
            .serviceType("C")
            .categoryId("E967O")
            .msg("더쇼")
            .resultCode("01")
            .regDt("2018-12-04 18:13:06")
            .pushYn("Y")
            .build();

        PushTargetEntity entity = PushTargetEntity.builder()
            .pKey(getKeyDto.getPKey())
            .regNo(getKeyDto.getRegNo())
            .saId(getKeyDto.getSaId())
            .stbMac(getKeyDto.getStbMac())
            .albumId(getKeyDto.getAlbumId())
            .categoryId(getKeyDto.getCategoryId())
            .serviceType(getKeyDto.getServiceType())
            .msg(getKeyDto.getMsg())
            .pushYn("N")
            .resultCode(getKeyDto.getResultCode())
            .regDt(getKeyDto.getRegDt() != null ? Timestamp.valueOf(getKeyDto.getRegDt()) : null)
            .modDt(Timestamp.valueOf(LocalDateTime.now()))
            .build();

        given(repository.getPushWithPkey(any())).willReturn(getKeyDto);
        given(repository.insertPush(any())).willReturn(entity);

        PushTargetEntity resultEntity = domainService.releasePush(requestDto);

        assertThat(resultEntity.getPushYn()).isEqualTo("N");
    }
}