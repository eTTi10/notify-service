package com.lguplus.fleta.data.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lguplus.fleta.data.dto.request.inner.HttpPushRequestDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;

/**
 * 단말 정보 요청 VO
 */
@Getter
@ApiModel(value = "단말 정보 요청 VO", description = "단말 정보 요청 VO")
@ToString
public class HttpPushRequestVo {
    @NotBlank
    @JsonProperty("service_type")
    @ApiModelProperty(position = 1, example = "H", value = "서비스타입(H:U+모바일TV)")
    private String serviceType;

    @NotBlank
    @JsonProperty("sa_id")
    @ApiModelProperty(position = 2, example = "\"1000000871\"", value = "가입자 번호", dataType = "string")
    private String saId;

    @NotBlank
    @JsonProperty("send_code")
    @ApiModelProperty(position = 3, example = "termsAgree", value = "push.yml에 미리정의되어있는 값")
    private String sendCode;

    @JsonProperty("items")
    @ApiModelProperty(position = 5, example = "[\"badge!^1\", \"sound!^ring.caf\", \"cm!^aaaa\"]", value = "추가할 항목(name!^value)")
    private List<String> items;

    @NotEmpty
    @JsonProperty("reserve")
    @ApiModelProperty(position = 6, example = "{\"service_push_status\":\"Y\"}", value = "push.yml에 미리정의되어있는 paramList 값")
    private Map<String, String> reserve;



    public HttpPushRequestDto convert() {
        return HttpPushRequestDto.builder()
                .saId(this.getSaId())
                .serviceType(this.getServiceType())
                .sendCode(this.getSendCode())
                .items(this.getItems())
                .reserve(this.getReserve())
                .build();
    }


}
