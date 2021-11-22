package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.PersonalizationDomainClient;
import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.response.RegistrationIdResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PersonalizationDomainFeinClient extends CommonDomainFeinClient implements PersonalizationDomainClient {

    private final PersonalizationFeinClient personalizationFeinClient;

    @Override
    public RegIdDto getRegistrationID(Map<String, String> sendPushInput) {

        return getResult(personalizationFeinClient.getRegistrationID(sendPushInput));
    }



}
