package com.lguplus.fleta.data.vo.musicshow;

import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.validation.AlphabetAndNumberPattern;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@Getter
@Setter
public class GetPushRequestVo {

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

    public GetPushRequestDto makeRefinedRequest(){
        return GetPushRequestDto.builder()
            .saId(this.sa_id)
            .stbMac(this.stb_mac)
            .albumId(this.album_id)
            .serviceType(GetPushRequestDto.SERVICE_TYPE.MUSIC_SHOW.CODE)
            .build();
    }

}
