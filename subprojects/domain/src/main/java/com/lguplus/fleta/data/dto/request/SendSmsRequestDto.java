package com.lguplus.fleta.data.dto.request;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class SendSmsRequestDto {

    private String sCtn;

    private String rCtn;

    private String msg;

}
