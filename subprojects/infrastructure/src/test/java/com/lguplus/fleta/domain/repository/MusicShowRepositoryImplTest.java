package com.lguplus.fleta.domain.repository;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import com.lguplus.fleta.data.entity.PushTargetEntity;
import com.lguplus.fleta.provider.jpa.MusicShowJpaEmRepository;
import com.lguplus.fleta.provider.jpa.MusicShowJpaRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MusicShowRepositoryImplTest {

    @InjectMocks
    MusicShowRepositoryImpl repositoryImpl;

    @Mock
    MusicShowJpaEmRepository emRepository;

    @Mock
    MusicShowJpaRepository jpaRepository;


    @Test
    void getPush() {

        GetPushDto dto = GetPushDto.builder()
            .pushYn("Y")
            .albumId("M01198F334PPV00")
            .resultCode("01")
            .startDt("201908161900")
            .build();

        given(emRepository.getPush(any())).willReturn(dto);

        PushRequestDto requestDto = PushRequestDto.builder()
            .saId("1000494369")
            .stbMac("v010.0049.4369")
            .albumId("M01198F334PPV00")
            .build();

        GetPushDto resultDto = repositoryImpl.getPush(requestDto);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getAlbumId()).isEqualTo(dto.getAlbumId());

    }

    @Test
    void getPushWithPkey() {
        PushRequestDto requestDto = PushRequestDto.builder()
            .saId("1000494369")
            .stbMac("v010.0049.4369")
            .albumId("M01198F334PPV00")
            .build();

        GetPushWithPKeyDto getKeyDto = GetPushWithPKeyDto.builder()
            .albumId("M01198F334PPV00")
            .build();

        given(emRepository.getPushWithPkey(any())).willReturn(getKeyDto);

        GetPushWithPKeyDto resultGetKeyDto = repositoryImpl.getPushWithPkey(requestDto);

        assertThat(resultGetKeyDto.getAlbumId()).isEqualTo("M01198F334PPV00");
    }

    @Test
    void insertPush() {

        PushTargetEntity entity = PushTargetEntity.builder()
            .pKey(0)
            .saId("500055344423")
            .regNo(11843)
            .stbMac("v000.5534.4423")
            .albumId("M0118C3162PPV00")
            .serviceType("C")
            .categoryId("E967O")
            .msg("더쇼")
            .resultCode("01")
            .pushYn("Y")
            .modDt(Timestamp.valueOf(LocalDateTime.now()))
            .build();

        given(jpaRepository.save(any())).willReturn(entity);

        PushTargetEntity resultEntity = repositoryImpl.insertPush(entity);

        assertThat(resultEntity.getPushYn()).isEqualTo("Y");
    }

    @Test
    void deletePush() {

        PushTargetEntity entity = PushTargetEntity.builder()
            .pKey(0)
            .saId("500055344423")
            .regNo(11843)
            .stbMac("v000.5534.4423")
            .albumId("M0118C3162PPV00")
            .serviceType("C")
            .categoryId("E967O")
            .msg("더쇼")
            .resultCode("01")
            .pushYn("N")
            .modDt(Timestamp.valueOf(LocalDateTime.now()))
            .build();

        given(jpaRepository.save(any())).willReturn(entity);

        PushTargetEntity resultEntity = repositoryImpl.insertPush(entity);

        assertThat(resultEntity.getPushYn()).isEqualTo("N");
    }

    @Test
    void getRegNoNextVal() {

        given(emRepository.getRegNoNextVal()).willReturn(1000);

        Integer regNoNextVal = repositoryImpl.getRegNoNextVal();

        assertThat(regNoNextVal).isEqualTo(1000);
    }

}