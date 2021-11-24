package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.RegIdDto;

import java.util.Map;

public interface PersonalizationDomainClient {

    RegIdDto getRegistrationID(Map<String, String> sendPushInput);
}
