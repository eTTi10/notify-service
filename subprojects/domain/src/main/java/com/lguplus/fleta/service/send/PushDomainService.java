package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.PersonalizationDomainClient;
import com.lguplus.fleta.client.SubscriberDomainClient;
import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;
import com.lguplus.fleta.exception.database.DataAlreadyExistsException;
import com.lguplus.fleta.repository.PushRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushDomainService {

    private final PersonalizationDomainClient personalizationDomainClient;
    private final SubscriberDomainClient subscriberDomainClient;

    public String getRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto) {

        Map<String, String> inputMap = new HashMap<>();

        inputMap.put("sa_id", sendPushCodeRequestDto.getSaId());
        inputMap.put("stb_mac", sendPushCodeRequestDto.getStbMac());

//        RegIdDto regIdDto = Optional.ofNullable(personalizationDomainClient.getRegistrationID(inputMap)).orElseThrow(DataAlreadyExistsException::new);
//        return regIdDto.getRegId();
        return "M00020200205"; // TODO 다른 도메인이 서비스 준비가 되어 실제, Feiin 연결될 경우 삭제

    }

    public String getRegistrationIDbyCtn(String ctn) {

        Map<String, String> inputMap = new HashMap<>();

        ctn = "0" + ctn;
        inputMap.put("ctn", ctn);

//        RegIdDto regIdDto = Optional.ofNullable(subscriberDomainClient.getRegistrationIDbyCtn(inputMap)).orElseThrow();
//        return regIdDto.getRegId();
        return "M00020200205"; // TODO 다른 도메인이 서비스 준비가 되어 실제, Feiin 연결될 경우 삭제

    }

}
