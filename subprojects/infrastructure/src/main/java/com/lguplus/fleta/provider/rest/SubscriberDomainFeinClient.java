package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.SubscriberDomainClient;
import com.lguplus.fleta.data.dto.RegIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubscriberDomainFeinClient extends CommonDomainFeinClient implements SubscriberDomainClient {

    private final SubscriberFeinClient subscriberFeinClient;

    @Override
    public RegIdDto getRegistrationIDbyCtn(Map<String, String> sendPushInput) {

//        return getResult(subscriberFeinClient.getRegistrationIDbyCtn(sendPushInput)); // TODO 다른 도메인이 서비스 준비가 되면 사용
        return RegIdDto.builder().registrationId("M00020200205").build(); 
    }
}
