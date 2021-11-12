package com.lguplus.fleta.provider.rest.multipush;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class MessageInfo {

    private int messageID;
    private byte[] bTransactionID;
    private String transactionID;
    private String channelID;
    private String result;
    private String data;
    private String statusCode;

    public MessageInfo(int messageID, String transactionID, String channelID, String result, String data, String statusCode) {
        this.messageID = messageID;
        this.transactionID = transactionID;
        this.channelID = channelID;
        this.result = result;
        this.data = data;
        this.statusCode = statusCode;
    }

}
