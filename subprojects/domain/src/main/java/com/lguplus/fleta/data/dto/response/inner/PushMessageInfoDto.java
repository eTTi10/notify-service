package com.lguplus.fleta.data.dto.response.inner;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class PushMessageInfoDto {
    private int messageID;
    private String transactionID; //12 char
    private String channelID;
    private String result;
    private String statusCode;
    private String destIp;
    private String data;            //Json

    public String getTransactionID() {
        return !StringUtils.isEmpty(transactionID) ? transactionID : "";
    }

    public String getData() {
        return !StringUtils.isEmpty(data) ? data : "";
    }

}
