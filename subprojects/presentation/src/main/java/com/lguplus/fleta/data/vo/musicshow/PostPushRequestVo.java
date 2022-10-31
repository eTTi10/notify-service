package com.lguplus.fleta.data.vo.musicshow;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.type.ServiceType;
import com.lguplus.fleta.exception.InvalidRequestTypeException;
import com.lguplus.fleta.exception.ParameterContainsNonAlphanumericException;
import com.lguplus.fleta.exception.ParameterContainsWhitespaceException;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import com.lguplus.fleta.exception.musicshow.MusicShowParameterOutOfRangeException;
import com.lguplus.fleta.validation.Groups;
import java.io.UnsupportedEncodingException;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Getter
@Setter

@GroupSequence({Groups.R1.class, Groups.C1.class, Groups.R2.class, Groups.C2.class, Groups.R3.class, Groups.C3.class,
    Groups.R4.class, Groups.C4.class, Groups.C5.class, Groups.R5.class, Groups.C6.class, Groups.R6.class, Groups.C8.class, Groups.C9.class, PostPushRequestVo.class})
public class PostPushRequestVo {

    @NotBlank(message = "sa_id 파라미터값이 전달이 안됨", groups = Groups.R1.class)
    @Size(max = 12, message = "파라미터 sa_id의 길이는 12 자리 이하 이어야 함", payload = MusicShowParameterOutOfRangeException.class, groups = Groups.C1.class)
    private final String sa_id; // 가입자정보

    @NotBlank(message = "stb_mac 파라미터값이 전달이 안됨", groups = Groups.R2.class)
    @Size(max = 38, message = "파라미터 stb_mac의 길이는 38 자리 이하 이어야 함", payload = MusicShowParameterOutOfRangeException.class, groups = Groups.C2.class)
    private final String stb_mac; // 가입자 STB MAC Address

    @NotBlank(message = "category_id 파라미터값이 전달이 안됨", groups = Groups.R3.class)
    @Size(max = 5, message = "파라미터 category_id의 길이는 5 자리 이하 이어야 함", payload = MusicShowParameterOutOfRangeException.class, groups = Groups.C3.class)
    private final String category_id; // 카테고리 ID

    @NotBlank(message = "album_id 파라미터값이 전달이 안됨", groups = Groups.R4.class)
    @Pattern(regexp = "^[^\\s]+$", message = "파라미터 album_id는 값에 공백이 없어야 함", payload = ParameterContainsWhitespaceException.class, groups = Groups.C4.class)
    @Pattern(regexp = "[a-zA-Z0-9]*$", message = "파라미터 album_id는 값에 영문,숫자만 포함되어야 함", payload = ParameterContainsNonAlphanumericException.class, groups = Groups.C5.class)
    @Size(max = 20, message = "파라미터 album_id의 길이는 20 자리 이하 이어야 함", payload = MusicShowParameterOutOfRangeException.class, groups = Groups.C5.class)
    private final String album_id; // 앨범 ID


    @NotBlank(message = "album_nm 파라미터값이 전달이 안됨", groups = Groups.R5.class)
    @Size(max = 200, message = "파라미터 album_nm의 길이는 200 자리 이하 이어야 함", payload = MusicShowParameterOutOfRangeException.class, groups = Groups.C6.class)
    private final String album_nm; // 보낼메시지(타이틀명) msg

    @NotBlank(message = "start_dt 파라미터값이 전달이 안됨", groups = Groups.R6.class)
    @Pattern(regexp = "[0-9]*$", message = "start_dt 파라미터는 숫자형 데이터이어야 함", payload = InvalidRequestTypeException.class, groups = Groups.C8.class)
    @Size(min = 12, max = 12, message = "파라미터 start_dt의 길이는 12 자리 이어야 함", payload = ParameterExceedMaxSizeException.class, groups = Groups.C9.class)
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
            throw new MusicShowParameterOutOfRangeException();
        }
    }

}

