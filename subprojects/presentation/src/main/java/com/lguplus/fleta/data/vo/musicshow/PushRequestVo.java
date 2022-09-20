package com.lguplus.fleta.data.vo.musicshow;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.type.ServiceType;
import com.lguplus.fleta.exception.ParameterContainsNonAlphanumericException;
import com.lguplus.fleta.exception.ParameterContainsWhitespaceException;
import com.lguplus.fleta.exception.musicshow.ParameterOutOfRangeException;
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
@GroupSequence({Groups.R1.class, Groups.C1.class, Groups.R2.class, Groups.C2.class, Groups.R3.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, PushRequestVo.class})
public class PushRequestVo {

    @NotBlank(message = "sa_id 파라미터값이 전달이 안됨", groups = Groups.R1.class)
    @Size(max = 12, message = "파라미터 sa_id의 길이는 12 자리 이하 이어야 함", payload = ParameterOutOfRangeException.class, groups = Groups.C1.class)
    private final String sa_id; // 가입자정보

    @NotBlank(message = "stb_mac 파라미터값이 전달이 안됨", groups = Groups.R2.class)
    @Size(max = 38, message = "파라미터 stb_mac의 길이는 38 자리 이하 이어야 함", payload = ParameterOutOfRangeException.class, groups = Groups.C2.class)
    private final String stb_mac; // 가입자 STB MAC Address

    @NotBlank(message = "album_id 파라미터값이 전달이 안됨", groups = Groups.R3.class)
    @Pattern(regexp = "^[^\\s]+$", message = "파라미터 album_id는 값에 공백이 없어야 함", payload = ParameterContainsWhitespaceException.class, groups = Groups.C3.class)
    @Pattern(regexp = "[a-zA-Z0-9]*$", message = "파라미터 album_id는 값에 영문숫자만 포함되어야 함", payload = ParameterContainsNonAlphanumericException.class, groups = Groups.C4.class)
    @Size(max = 20, message = "파라미터 album_id의 길이는 20 자리 이하 이어야 함", groups = Groups.C5.class)
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
