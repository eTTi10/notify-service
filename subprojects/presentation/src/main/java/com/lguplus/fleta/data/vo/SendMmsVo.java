package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.exception.ParameterLengthOverLimitException;
import com.lguplus.fleta.exception.ParameterMissingException;
import com.lguplus.fleta.exception.ParameterPhoneNumberErrorException;
import com.lguplus.fleta.exception.ParameterTypeMismatchException;
import com.lguplus.fleta.validation.Groups;
import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

//todo groups 규칙에 맞춰 r1, r2 이런식으로 수정
@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class,
    Groups.C7.class, Groups.C8.class, Groups.C9.class, Groups.C10.class, Groups.C11.class, Groups.C12.class,
    Groups.C13.class, Groups.C14.class, Groups.C15.class, Groups.C16.class, Groups.C17.class,
    SendMmsVo.class})
public class SendMmsVo {

    @ParamAlias("sa_id")
    @NotBlank(message = "sa_id 파라미터값이 전달이 안됨", groups = Groups.C1.class)//5008
    @Pattern(regexp = "[A-Za-z0-9]*$", message = "sa_id의 패턴이 일치하지 않습니다.", payload = ParameterTypeMismatchException.class, groups = Groups.C5.class)//5008
    @Size(min = 8, message = "sa_id 의 길이가 8보다 작습니다.", payload = ParameterLengthOverLimitException.class, groups = Groups.C9.class)//5002
    @Size(max = 15, message = "sa_id 의 길이가 10보다 큽니다.", payload = ParameterLengthOverLimitException.class, groups = Groups.C12.class)//5002 15인데 메세지만 10으로 표시
    private String saId;

    @ParamAlias("stb_mac")
    @NotBlank(message = "stb_mac 파라미터값이 전달이 안됨", payload = ParameterMissingException.class, groups = Groups.C2.class)// 5000으로 수정 해야함
    @Pattern(regexp = "[a-zA-Z0-9.]*$", message = "stb_mac의 패턴이 일치하지 않습니다.", payload = ParameterTypeMismatchException.class, groups = Groups.C6.class)//5008
    @Size(min = 10, message = "stb_mac 의 길이가 10보다 작습니다.", payload = ParameterTypeMismatchException.class, groups = Groups.C10.class)//5008
    @Size(max = 20, message = "stb_mac 의 길이가 20보다 큽니다.", payload = ParameterLengthOverLimitException.class, groups = Groups.C13.class)//5008
    private String stbMac;

    @ParamAlias("ctn")
    @NotBlank(message = "필수 요청 정보 누락(ctn 가 Null 혹은 빈값 입니다.)", groups = Groups.C3.class)
    @PositiveOrZero(message = "ctn의 패턴이 일치하지 않습니다.", payload = ParameterTypeMismatchException.class, groups = Groups.C7.class)//{ 양수 또는 0} 허용
    @Pattern(regexp = "01([016789])([0-9]{3,4})([0-9]{4})$", message = "전화번호 형식 오류", payload = ParameterPhoneNumberErrorException.class, groups = Groups.C11.class)//asis : flag.phone_number_error = 1502 => 5008로 대체
    private String ctn;

    @ParamAlias("mms_cd")
    @NotBlank(message = "필수 요청 정보 누락(mms_cd 가 Null 혹은 빈값 입니다.)", groups = Groups.C4.class) //todo error, code 5000으로 수정
    @Pattern(regexp = "[A-Za-z0-9]*$", message = "mms_cd의 패턴이 일치하지 않습니다.", payload = ParameterTypeMismatchException.class, groups = Groups.C8.class)//5008
    private String mmsCd;

    @ParamAlias("replacement")
    private String replacement;

    public SendMmsRequestDto convert() {
        return SendMmsRequestDto.builder()
            .saId(this.getSaId())
            .stbMac(this.getStbMac())
            .mmsCd(this.getMmsCd())
            .ctn(this.getCtn())
            .replacement(this.getReplacement())
            .build();
    }


}

