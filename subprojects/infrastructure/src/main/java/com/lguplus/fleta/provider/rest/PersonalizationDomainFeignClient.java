package com.lguplus.fleta.provider.rest;

import com.lguplus.fleta.client.PersonalizationDomainClient;
import com.lguplus.fleta.data.dto.RegIdDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonalizationDomainFeignClient extends CommonDomainFeignClient implements PersonalizationDomainClient {

    private final PersonalizationFeignClient personalizationFeignClient;

    @Override
    public RegIdDto getRegistrationID(Map<String, String> sendPushInput) {

        return getResult(personalizationFeignClient.getRegistrationID(sendPushInput));
    }
}
