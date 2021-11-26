package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.response.SuccessResponseDto;

public interface SmsGatewayClient {

    void sendMessage(String orgAddr, String dstAddr, String callBack, String message, int sn);

}
