package com.lguplus.fleta.data.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class SendSmsCodeRequestDto {

    private String saId;

    private String stbMac;

    private String smsCd;

    private String ctn;

    private String replacement;
}
