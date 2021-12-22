package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.RegIdDto;

import java.util.Map;

public interface SubscriberDomainClient {

    RegIdDto getRegistrationIDbyCtn(Map<String, String> sendPushInput);
}
