package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

public interface SmsAgentDomainClient {

    SmsGatewayResponseDto send(String s_ctn, String r_ctn, String msg) throws UnsupportedEncodingException, ExecutionException, InterruptedException;
}
