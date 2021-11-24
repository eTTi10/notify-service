package com.lguplus.fleta.provider.rest.multipush;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//@NoArgsConstructor
@Builder
@Getter
public class MessageInfo {
    private int messageID;
    private byte[] bTransactionID;
    private String transactionID;
    private String channelID;
    private String result;
    private String data;
    private String statusCode;

    private String transactionDate;
    private int transactionSeq;
/*
    public MessageInfo(int messageID, String transactionID, String channelID, String result, String data, String statusCode) {
        this.messageID = messageID;
        this.transactionID = transactionID;
        this.channelID = channelID;
        this.result = result;
        this.data = data;
        this.statusCode = statusCode;
    }
    */

}
