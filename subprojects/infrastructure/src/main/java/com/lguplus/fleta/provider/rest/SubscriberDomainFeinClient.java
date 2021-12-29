package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.PersonalizationDomainClient;
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

//        return getResult(subscriberFeinClient.getRegistrationIDbyCtn(sendPushInput));
        return RegIdDto.builder().regId("M00020200205").build(); // TODO 다른 도메인이 서비스 준비가 되어 실제, Feiin 연결될 경우 삭제
    }
}
