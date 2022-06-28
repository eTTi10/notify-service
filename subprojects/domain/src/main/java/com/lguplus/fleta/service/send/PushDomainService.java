package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.PersonalizationDomainClient;
import com.lguplus.fleta.client.SubscriberDomainClient;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SendPushResponseDto;
import com.lguplus.fleta.properties.SendPushCodeProps;
import com.lguplus.fleta.service.httppush.HttpSinglePushDomainService;
import com.lguplus.fleta.service.push.PushSingleDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushDomainService {

    private final HttpSinglePushDomainService httpSinglePushDomainService;
    private final PushSingleDomainService pushSingleDomainService;
    private final PersonalizationDomainClient personalizationDomainClient;
    private final SubscriberDomainClient subscriberDomainClient;
    private final SendPushCodeProps sendPushCodeProps;

    @Value("${push.fcm.extra.send}")
    private String fcmExtraSend;

    @Value("${push.fcm.extra.serviceid}")
    private String extraServiceId;

    @Value("${push.fcm.extra.appid}")
    private String extraApplicationId;

    /**
     * code를 이용한 push발송
     *
     * @param sendPushCodeRequestDto
     * @return
     */
    public SendPushResponseDto sendPushCode(SendPushCodeRequestDto sendPushCodeRequestDto) {
        PushSender pushSender = new PushSender(httpSinglePushDomainService
            , pushSingleDomainService, personalizationDomainClient, subscriberDomainClient
            , sendPushCodeProps, fcmExtraSend, extraServiceId, extraApplicationId);
        return pushSender.sendPushCode(sendPushCodeRequestDto);
    }

}
