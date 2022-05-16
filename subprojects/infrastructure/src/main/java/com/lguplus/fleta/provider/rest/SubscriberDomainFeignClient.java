package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.SubscriberDomainClient;
import com.lguplus.fleta.data.dto.SaIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubscriberDomainFeignClient extends CommonDomainFeignClient implements SubscriberDomainClient {

    private final SubscriberFeignClient subscriberFeignClient;

    @Override
    public List<SaIdDto> getRegistrationIDbyCtn(Map<String, String> sendPushInput) {

        return getResult(subscriberFeignClient.getRegistrationIDbyCtn(sendPushInput));
    }
}
