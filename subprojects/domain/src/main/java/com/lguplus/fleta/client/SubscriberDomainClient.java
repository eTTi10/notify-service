package com.lguplus.fleta.client;

import com.lguplus.fleta.data.dto.SaIdDto;
import java.util.List;
import java.util.Map;

public interface SubscriberDomainClient {

    List<SaIdDto> getRegistrationIDbyCtn(Map<String, String> sendPushInput);
}
