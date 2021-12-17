package com.lguplus.fleta.data.dto.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class SendSmsCodeRequestDto {

    private String saId;

    private String stbMac;

    private String smsCd;

    private String ctn;

    private String replacement;
}
