package com.lguplus.fleta.data.dto.request;

import lombok.*;

/**
 * MmsAgentDomainService.sendMmsCode에서
 * apiClient.callSettingApi통해서 가져온 메세지 내용을 기반으로
 * mms전송처리 전문을 생성해주는 Dto
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MmsRequestDto {
    private String ctn;
    private String mmsTitle;
    private String mmsMsg;
    private String mmsRep;
}
