package com.lguplus.fleta.provider.socket.multi;

import lombok.Builder;
import lombok.Getter;

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
}
