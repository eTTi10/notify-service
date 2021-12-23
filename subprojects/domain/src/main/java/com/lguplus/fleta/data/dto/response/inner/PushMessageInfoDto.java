package com.lguplus.fleta.data.dto.response.inner;

import lombok.*;

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
    private String data;            //Json
    private String statusCode;

    private String transactionDate;
    private int transactionSeq;

    private String destIp;

    public String getTransactionID() {
        return "" + transactionID;
    }

    public String getData() {
        return "" + data;
    }

}
