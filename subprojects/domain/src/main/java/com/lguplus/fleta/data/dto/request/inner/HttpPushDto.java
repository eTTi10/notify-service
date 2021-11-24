package com.lguplus.fleta.data.dto.request.inner;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Getter
@ToString
@SuperBuilder
public class HttpPushDto {

    /** Push 요청 서버 타입 */
    private String requestPart;

    /** 요청 시각 YYYYMMDDhhmmss */
    private String requestTime;

    /** Transaction ID (YYYYMMDD+Sequence Number(4byte)) */
    private String pushId;

    /** 서비스 등록시 부여받은 Unique ID */
    private String serviceId;

    /** 서비스 등록시 등록된 비밀번호 (SHA-512 암호화) */
    private String servicePass;

    /** 어플리케이션 ID */
    private String applicationId;

    /** Push 를 전송할 서비스 키(reg_id) */
   private String serviceKey;

    /** Push Notification 과는 무관 OMS 로그 구분용 (사용안함) */
    @Builder.Default private String subServiceId = "";

    /** Json format의 실제 발송 메시지 */
    private String payload;

}
