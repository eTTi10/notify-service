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
@ToString
public class PushResponseDto {

    private String msgId;
    private String pushId;
    private String statusCode;
    private String statusMsg;
    private String responseCode;

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

}
