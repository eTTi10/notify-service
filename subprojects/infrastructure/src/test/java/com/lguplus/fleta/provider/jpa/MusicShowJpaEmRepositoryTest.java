package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import com.lguplus.fleta.testutil.DtoConverterTestUtil;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicShowJpaEmRepositoryTest {

    @Spy
    @InjectMocks
    MusicShowJpaEmRepository repository;

    @Mock
    EntityManager em;

    @Mock
    Query query;

    @Mock
    NativeQuery<?> nativeQuery;

    private void mockNativeQueryMethodOfEntityManager() {
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.unwrap(NativeQuery.class)).thenReturn(nativeQuery);
        org.hibernate.query.Query hibernateQuery = mock(org.hibernate.query.Query.class);
        when(nativeQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)).thenReturn(hibernateQuery);
    }

    @Test
    void getPush() {

        GetPushDto dto = GetPushDto.builder()
            .pushYn("Y")
            .albumId("M01198F334PPV00")
            .resultCode("01")
            .startDt("201908161900")
            .build();

        PushRequestDto requestDto = PushRequestDto.builder()
            .saId("1000494369")
            .stbMac("v010.0049.4369")
            .albumId("M01198F334PPV00")
            .serviceType("C")
            .build();

        mockNativeQueryMethodOfEntityManager();

        when(repository.convertSingle(query, GetPushDto.class)).thenReturn(Optional.of(dto));

        DtoConverterTestUtil.testMockedDtoConverterForSingle(() -> {

            GetPushDto result = repository.getPush(requestDto);

            assertThat(result).isNotNull();
            assertThat(result.getAlbumId()).isEqualTo(requestDto.getAlbumId());
        }, Optional.of(dto));
    }

    @Test
    void getPushWithPkey() {

        GetPushWithPKeyDto dto = GetPushWithPKeyDto.builder()
            .albumId("M01198F334PPV00")
            .build();

        PushRequestDto requestDto = PushRequestDto.builder()
            .saId("1000494369")
            .stbMac("v010.0049.4369")
            .albumId("M01198F334PPV00")
            .build();

        mockNativeQueryMethodOfEntityManager();

        when(repository.convertSingle(query, GetPushWithPKeyDto.class)).thenReturn(Optional.of(dto));

        DtoConverterTestUtil.testMockedDtoConverterForSingle(() -> {

            GetPushWithPKeyDto result = repository.getPushWithPkey(requestDto);

            assertThat(result).isNotNull();
            assertThat(result.getAlbumId()).isEqualTo(requestDto.getAlbumId());
        }, Optional.of(dto));

    }


    @Test
    void getRegNoNextVal() {

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1);

        Integer result = repository.getRegNoNextVal();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1);

    }


}