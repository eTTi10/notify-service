package com.lguplus.fleta.data.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "type", "s_flag" , "s_message"})
public class PushServiceResultDto {

    @JsonProperty(value = "type")
    private String type;

    @JsonProperty(value = "s_flag")
    private String flag;

    @JsonProperty(value = "s_message")
    private String message;
}
