package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

public interface SmsAgentClient {

    SmsGatewayResponseDto send(String sCtn, String rCtn, String message) throws UnsupportedEncodingException, ExecutionException, InterruptedException;
}
