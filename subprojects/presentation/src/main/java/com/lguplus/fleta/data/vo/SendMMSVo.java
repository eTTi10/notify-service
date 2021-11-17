package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.data.dto.request.SendSMSRequestDto;
import com.lguplus.fleta.exception.ParameterContainsNonAlphanumericException;
import com.lguplus.fleta.exception.ParameterContainsWhitespaceException;
import com.lguplus.fleta.exception.ParameterExceedMaxSizeException;
import com.lguplus.fleta.exception.ParameterOverBoundsException;
import lombok.Getter;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class SendMMSVo {

    /*
    @RequestParam(value = "sa_id", required = false, defaultValue = "") String sa_id,
    @RequestParam(value = "stb_mac", required = false, defaultValue = "") String stb_mac,
    @RequestParam(value = "mms_cd", required = false, defaultValue = "") String mms_cd,
    @RequestParam(value = "ctn", required = false, defaultValue = "") String ctn,
    @RequestParam(value = "replacement", required = false, defaultValue = "") String replacement,
    */

    @Pattern(regexp = "^[^\\s]+$", message = "파라미터 sa_id는 값에 공백이 없어야 함", payload = ParameterContainsWhitespaceException.class)
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "파라미터 sa_id는 값에 영문,숫자만 포함되어야 함", payload = ParameterContainsNonAlphanumericException.class)
    @Size(min=12, max=12, message = "파라미터 sa_id의 길이는 12 자리 이어야 함", payload = ParameterExceedMaxSizeException.class)
    //@NotBlank(message = "sa_id 파라미터값이 전달이 안됨")
    @ParamAlias("sa_id")
    private String saId;

    @Pattern(regexp = "^([\\w.]+|\\s*)?$", message = "잘못된 포맷 형식 전달 및 응답 결과 지원하지 않는 포맷(stb_mac 의 패턴이 일치하지 않습니다.)")
    @Size(max=38, message = "파라미터 stb_mac는 값이 38 이하이어야 함", payload = ParameterOverBoundsException.class)
    //@NotBlank(message = "stb_mac 파라미터값이 전달이 안됨")
    @ParamAlias("stb_mac")
    private String stbMac;

    @NotBlank(message = "sms_cd 필수입니다.")
    @ParamAlias("mms_cd")
    private String mmsCd;

    @NotBlank(message = "ctn 필수입니다.")
    @ParamAlias("ctn")
    private String ctn;

    @ParamAlias("replacement")
    private String replacement;

    public SendMMSRequestDto convert(){

        return SendMMSRequestDto.builder()
                .saId(getSaId())
                .stbMac(getStbMac())
                .mmsCd(getMmsCd())
                .ctn(getCtn())
                .replacement(getReplacement())
                .build();
    }

}
