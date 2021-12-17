package com.lguplus.fleta.data.dto.response.inner;

import lombok.*;


/**
 * Push 응답결과 DTO
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class PushResponseDto {

    private String msgId;
    private String pushId;
    private String statusCode;
    private String statusMsg;

}
