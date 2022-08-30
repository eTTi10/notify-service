package com.lguplus.fleta.data.vo.musicshow;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.type.ServiceType;
import com.lguplus.fleta.exception.ParameterTypeMismatchException;
import com.lguplus.fleta.validation.AlphabetAndNumberOrEmptyPattern;
import com.lguplus.fleta.validation.Groups;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class,
    Groups.C7.class, Groups.C8.class, Groups.C9.class, Groups.C10.class, PushRequestVo.class})
public class PushRequestVo {

    @NotBlank(message = "sa_id 파라미터값이 전달이 안됨", groups = Groups.C1.class) //5008
    @AlphabetAndNumberOrEmptyPattern(groups = Groups.C4.class)
    @Size(max = 12, message = "파라미터 sa_id의 길이는 12 자리 이하 이어야 함", groups = Groups.C7.class)
    private final String sa_id; // 가입자정보

    @NotBlank(message = "stb_mac 파라미터값이 전달이 안됨", groups = Groups.C2.class) //5008
    @AlphabetAndNumberOrEmptyPattern(groups = Groups.C5.class)
    @Size(max = 38, message = "파라미터 stb_mac의 길이는 38 자리 이하 이어야 함", groups = Groups.C8.class)
    private final String stb_mac; // 가입자 STB MAC Address

    @NotBlank(message = "album_id 파라미터값이 전달이 안됨", groups = Groups.C3.class) //5008
    @AlphabetAndNumberOrEmptyPattern(groups = Groups.C6.class)
    @Pattern(regexp = "[a-zA-Z0-9]*$", message = "album_id의 패턴이 일치하지 않습니다.", payload = ParameterTypeMismatchException.class, groups = Groups.C9.class)//5008
    @Size(max = 20, message = "파라미터 album_id의 길이는 20 자리 이하 이어야 함", groups = Groups.C10.class)
    private final String album_id; // 앨범 ID

    public PushRequestDto makeRefinedGetRequest() {
        return PushRequestDto.builder()
            .saId(this.sa_id)
            .stbMac(this.stb_mac)
            .albumId(this.album_id)
            .serviceType(ServiceType.MUSIC_SHOW.getCode())
            .build();
    }

    public PushRequestDto makeRefinedReleaseRequest() {
        return PushRequestDto.builder()
            .saId(this.sa_id)
            .stbMac(this.stb_mac)
            .albumId(this.album_id)
            .pushYn(PushRequestDto.CONST.RELEASE.CODE)
            .serviceType(ServiceType.MUSIC_SHOW.getCode())
            .build();
    }
}
