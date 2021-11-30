package com.lguplus.fleta.data.dto.request.inner;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@Getter
@ToString
@SuperBuilder
public class SmsAgentRequestDto {

    /**  */
    private Integer ptDay;

    /**  */
    private Integer rowSeq;

    /**  */
    private String smsCd;

    /**  */
    private String smsId;

    /**  */
    private String procFlag;

    /**  */
    private String replacement;

    /**  */
    private String smsMsg;

}
