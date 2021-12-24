package com.lguplus.fleta.data.dto.response.inner;

import lombok.*;

import java.util.List;


/**
 * Push 응답결과 DTO
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class PushMultiResponseDto {

    private String msgId;
    private String pushId;
    private String statusCode;
    private String statusMsg;

    private String regId;

    private List<String> failUsers;

}
