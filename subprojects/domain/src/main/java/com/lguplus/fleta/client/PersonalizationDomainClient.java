package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.response.RegistrationIdResponseDto;

import java.util.Map;

public interface PersonalizationDomainClient {

    RegistrationIdResponseDto getRegistrationID(Map<String, String> sendPushInput);
}
