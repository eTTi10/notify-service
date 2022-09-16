package com.lguplus.fleta.provider.jpa;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

//@SpringBootTest(classes = {BootConfig.class})
@ExtendWith(SpringExtension.class)
class MusicShowJpaEmRepositoryTest {

    //    @Autowired
    //    MusicShowJpaEmRepository emRepository;
    @InjectMocks
    MusicShowJpaEmRepository emRepository;

    @Mock
    Query query;
    @Mock
    private EntityManager em;
    @Mock
    org.hibernate.query.Query hibernateQuery;

    @BeforeEach
    void setUp() {
        //        doReturn(query).when(em).createNativeQuery(anyString(), (Class) any());
        //        doReturn(query).when(query).setParameter(anyString(), any());
        //        when(em.createNativeQuery(anyString())).thenReturn(query);
        //        when(query.setParameter(anyString(), any())).thenReturn(query);
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

        GetPushDto resultDto = this.emRepository.getPush(requestDto);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getAlbumId()).isEqualTo(dto.getAlbumId());
    }

    @Test
    void validAlbumId() {

        Integer count = this.emRepository.validAlbumId("M01198F334PPV00");

        assertThat(count).isEqualTo(1);
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

        GetPushWithPKeyDto resultGetKeyDto = this.emRepository.getPushWithPkey(requestDto);

        assertThat(resultGetKeyDto.getAlbumId()).isEqualTo("M01198F334PPV00");
    }

    @Test
    void getRegNoNextVal() {

        given(emRepository.getRegNoNextVal()).willReturn(1000);

        Integer regNoNextVal = this.emRepository.getRegNoNextVal();

        assertThat(regNoNextVal).isEqualTo(1000);
    }

}