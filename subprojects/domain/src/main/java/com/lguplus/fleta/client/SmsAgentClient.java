package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;

public interface SmsAgentClient {

    SmsGatewayResponseDto send(String sCtn, String rCtn, String message);
}
