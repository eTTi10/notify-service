package com.lguplus.fleta.data.dto.response.inner;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * Push 응답결과 DTO
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class PushMultiResponseDto {

    private String messageId;
    private String pushId;
    private String statusCode;
    private String statusMsg;

    private String regId;

    private List<String> failUsers;

}
