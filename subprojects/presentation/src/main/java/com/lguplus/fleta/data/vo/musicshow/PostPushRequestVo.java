package com.lguplus.fleta.data.vo.musicshow;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.type.ServiceType;
import com.lguplus.fleta.exception.ParameterLengthOverLimitException;
import com.lguplus.fleta.validation.AlphabetAndNumberPattern;
import com.lguplus.fleta.validation.NumberPattern;
import java.io.UnsupportedEncodingException;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

@Slf4j
@AllArgsConstructor
@Getter
@Setter
public class PostPushRequestVo {

    @AlphabetAndNumberPattern
    @Length(min = 7, max = 12)
    @NotBlank
    private final String sa_id; // 가입자정보

    @AlphabetAndNumberPattern
    @Length(min = 14, max = 14)
    @NotBlank
    private final String stb_mac; // 가입자 STB MAC Address

    @AlphabetAndNumberPattern
    @Length(min = 15, max = 15)
    @NotBlank
    private final String album_id; // 앨범 ID

    @AlphabetAndNumberPattern
    @Length(min = 1, max = 5)
    @NotBlank
    private final String category_id; // 카테고리 ID

    //    @AlphabetAndNumberPattern
    @Length(min = 1, max = 200)
    @NotBlank
    private final String album_nm; // 보낼메시지(타이틀명) msg

    @NumberPattern
    @Length(min = 12, max = 12)
    @NotBlank
    private final String start_dt; // 공연시작일(푸시발송예정일) send_dt


    public PushRequestDto makeRefinedPostRequest() {
        validAlbumNm(this.album_nm, 12);
        return PushRequestDto.builder()
            .saId(this.sa_id)
            .stbMac(this.stb_mac)
            .albumId(this.album_id)
            .categoryId(this.category_id)
            .msg(this.album_nm)
            .sendDt(this.start_dt)
            .pushYn(PushRequestDto.CONST.REG.CODE)
            .serviceType(ServiceType.MUSIC_SHOW.getCode())
            .build();
    }

    protected void validAlbumNm(String album_nm, int length) {
        String value = album_nm;
        int valueSize = 0;
        try {
            valueSize = value.getBytes("MS949").length;
        } catch (UnsupportedEncodingException e) {
            log.error("[뮤직공연][앨범명]MS949타입의 Byte Size 계산 실패");
            valueSize = value.getBytes().length;
        }
        if (valueSize > length) {
            throw new ParameterLengthOverLimitException();
        }
    }

}

