package com.lguplus.fleta.data.dto.response.inner;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class PushMessageInfoDto {
    private int messageId;
    private String transactionId; //12 char
    private String channelId;
    private String result;
    private String statusCode;
    private String destinationIp;
    private String data;            //Json

    public String getTransactionId() {
        return !StringUtils.isEmpty(transactionId) ? transactionId : "";
    }

    public String getData() {
        return !StringUtils.isEmpty(data) ? data : "";
    }

}
