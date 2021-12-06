package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.request.MmsRequestDto;

import java.util.Map;

public interface MmsAgentDomainClient {
    String sendMMS(Map<String, ?> mmsConfig, MmsRequestDto mmsDto);
}
