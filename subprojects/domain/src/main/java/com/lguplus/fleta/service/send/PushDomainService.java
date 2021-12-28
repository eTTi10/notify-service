package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.PersonalizationDomainClient;
import com.lguplus.fleta.client.SubscriberDomainClient;
import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException;
import com.lguplus.fleta.properties.SendPushCodeProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.mapper.Mapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushDomainService {

    @Value("${fcm.extra.serviceid}")
    private String extraServiceId;

    @Value("${fcm.extra.appid}")
    private String extraAppId;

    private final PersonalizationDomainClient personalizationDomainClient;
    private final SubscriberDomainClient subscriberDomainClient;
    private final SendPushCodeProps sendPushCodeProps;

    public String getRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto) {

        Map<String, String> inputMap = new HashMap<>();

        inputMap.put("sa_id", sendPushCodeRequestDto.getSaId());
        inputMap.put("stb_mac", sendPushCodeRequestDto.getStbMac());

        RegIdDto regIdDto = Optional.ofNullable(personalizationDomainClient.getRegistrationID(inputMap)).orElseThrow();
        return regIdDto.getRegId();

    }

    public String getRegistrationIDbyCtn(String ctn) {

        Map<String, String> inputMap = new HashMap<>();

        ctn = "0" + ctn;
        inputMap.put("ctn", ctn);

        RegIdDto regIdDto = Optional.ofNullable(subscriberDomainClient.getRegistrationIDbyCtn(inputMap)).orElseThrow();
        return regIdDto.getRegId();

    }

    public HttpPushSingleRequestDto getGcmOrTVRequestDto(SendPushCodeRequestDto sendPushCodeRequestDto, String serviceTarget) {

        String regId = sendPushCodeRequestDto.getRegId();
        String pushType = sendPushCodeRequestDto.getPushType();
        String sendCode = sendPushCodeRequestDto.getSendCode();
        String regType = sendPushCodeRequestDto.getRegType();
        List items = sendPushCodeRequestDto.getItems();

        Map<String, String> paramMap = sendPushCodeRequestDto.getReserve();
        log.debug("sendPushCodeRequestDto.getReserve() : {}", sendPushCodeRequestDto.getReserve());
        log.debug("paramMap : {}", paramMap);

        String paramList;
        String[] pushParamList;
        int paramSize =0;

        //serviceType에 따라 다른 appId와 serviceId를 가져오는데, serviceType이 빈 값이거나 H 일경우  default 값을 셋팅한다
        Map<String, String> appInfoDefaultMap = sendPushCodeProps.findMapByServiceType("default").orElseThrow();

        //입력받은 sendCode 를 이용해 푸시발송에 필요한 정보를 가져온다
        Map<String, String> pushInfoMap = sendPushCodeProps.findMapBySendCode(sendCode).orElseThrow(() -> new InvalidSendPushCodeException("send code 미지원"));
        //serviceType에 따라 다른 appId와 serviceId를 가져온다
        Map<String, String> appInfoMap = sendPushCodeProps.findMapByServiceType(serviceTarget).orElse(Map.of());

        String serviceId = appInfoMap.get("gcm.serviceid");
        String appId = appInfoMap.get("gcm.appid");
        String payload = pushInfoMap.get("gcm.payload.body");
        String payloadItem = pushInfoMap.get("apns.payload.item");  //APNS전용 추가 item


        if (serviceTarget.equals("") || serviceTarget.equals("H")) {

            serviceId = appInfoDefaultMap.get("gcm.serviceid");
            appId = appInfoDefaultMap.get("gcm.appid");
        }

        log.debug("sendPushCtn Property Data Check : {} {} {}", serviceId, appId, payload);

        //reserve에 들어갈 내용을
        paramList = pushInfoMap.get("param.list");;
        pushParamList = paramList.split("\\|");
        paramSize = pushParamList.length;


        for (int index = 0; index < paramSize; index++) {
            try {
                payload = payload.replace("[+" + pushParamList[index] + "]", paramMap.get(pushParamList[index]));
            } catch (Exception e) {
                payload = payload.replace("[+" + pushParamList[index] + "]", "");
            }
        }

        regId = (regType.equals("2")) ? getRegistrationIDbyCtn(regId) : regId;

        if(pushType.equals("A")) {
            //APNS일 경우 items의 맨 앞에 payloaditem를 끼워 넣는다.
            items.add(0, payloadItem);
        }

        return HttpPushSingleRequestDto.builder()
                .appId(appId)
                .serviceId(serviceId)
                .pushType(pushType)
                .msg(payload)
                .users(List.of(regId))
                .items(items)
                .build();
    }

    public HttpPushSingleRequestDto getApnsRequestDto(SendPushCodeRequestDto sendPushCodeRequestDto, String serviceTarget) {

        String regId = sendPushCodeRequestDto.getRegId();
        String pushType = sendPushCodeRequestDto.getPushType();
        String sendCode = sendPushCodeRequestDto.getSendCode();
        String regType = sendPushCodeRequestDto.getRegType();
        List items = sendPushCodeRequestDto.getItems();
        Map<String, String> paramMap = sendPushCodeRequestDto.getReserve();
        log.debug("sendPushCodeRequestDto.getReserve() : {}", sendPushCodeRequestDto.getReserve());
        log.debug("paramMap : {}", paramMap);

        //입력받은 sendCode 를 이용해 푸시발송에 필요한 정보를 가져온다
        Map<String, String> pushInfoMap = sendPushCodeProps.findMapBySendCode(sendCode).orElseThrow(() -> new InvalidSendPushCodeException("send code 미지원"));
        //serviceType에 따라 다른 appId와 serviceId를 가져오며 serviceType이 빈 값이거나 H 일경우  default 값을 셋팅한다
        Map<String, String> appInfoDefaultMap = sendPushCodeProps.findMapByServiceType("default").orElseThrow();
        //serviceType에 따라 다른 appId와 serviceId를 가져온다
        Map<String, String> appInfoMap = sendPushCodeProps.findMapByServiceType(serviceTarget).orElse(Map.of());

        String serviceId = appInfoMap.get("apns.serviceid");
        String appId = appInfoMap.get("apns.appid");
        String payload = pushInfoMap.get("apns.payload.body");
        String payloadItem = pushInfoMap.get("apns.payload.item");  //APNS전용 추가 item

        if (serviceTarget.equals("") || serviceTarget.equals("H")) {
            serviceId = appInfoDefaultMap.get("apns.serviceid");
            appId = appInfoDefaultMap.get("apns.appid");
        }

        //reserve에 들어갈 내용을
        String paramList = pushInfoMap.get("param.list");;
        String[] pushParamList = paramList.split("\\|");
        int paramSize = pushParamList.length;

        log.debug("pushinfo.{}.param.list : {}", sendCode, pushParamList);

        log.debug("sendPushCtn Property Data Check : {} {} {}", serviceId, appId, payload, payloadItem);

        for (int index = 0; index < paramSize; index++) {
            try {
                payloadItem = payloadItem.replace("[+" + pushParamList[index] + "]", paramMap.get(pushParamList[index]));
            } catch (Exception e) {
                payloadItem = payloadItem.replace("[+" + pushParamList[index] + "]", "");
            }
        }

        regId = (regType.equals("2")) ? getRegistrationIDbyCtn(regId) : regId;

        //APNS일 경우 items의 맨 앞에 payloaditem를 끼워 넣는다.
        items.add(0, payloadItem);

        return HttpPushSingleRequestDto.builder()
                .appId(appId)
                .serviceId(serviceId)
                .pushType(pushType)
                .msg(payload)
                .users(List.of(regId))
                .items(items)
                .build();
    }

    public HttpPushSingleRequestDto getPosRequestDto(SendPushCodeRequestDto sendPushCodeRequestDto, String serviceTarget) {

        String regId = sendPushCodeRequestDto.getRegId();
        String pushType = sendPushCodeRequestDto.getPushType();
        String sendCode = sendPushCodeRequestDto.getSendCode();
        String regType = sendPushCodeRequestDto.getRegType();
        List items = sendPushCodeRequestDto.getItems();

        String paramList;
        String[] pushParamList;
        Map<String, String> paramMap = new HashMap<String, String>();
        int paramSize =0;

        //serviceType에 따라 다른 appId와 serviceId를 가져온다
        Map<String, String> appInfoMap = sendPushCodeProps.findMapByServiceType(serviceTarget).orElse(Map.of());

        String serviceId = Optional.of(appInfoMap.get("pos.serviceid")).orElseThrow(() -> new InvalidSendPushCodeException("LG Push 미지원"));
        String appId = Optional.of(appInfoMap.get("pos.appid")).orElseThrow(() -> new InvalidSendPushCodeException("LG Push 미지원"));

        //reg_id를 기입하지 않았다면 DB에서 RegID를 찾아서 처리한다.
        regId = StringUtils.defaultIfEmpty(regId, getRegistrationID(sendPushCodeRequestDto));
        String bodyLgPush = ""; // TODO LG PUSH 사용한다면 RequestBody로 받은 String을 받아와야 한다.
        return HttpPushSingleRequestDto.builder()
                .appId(appId)
                .serviceId(serviceId)
                .pushType(pushType)
                .msg(bodyLgPush)
                .users(List.of(regId))
                .items(items)
                .build();
    }

    public PushRequestSingleDto getExtraPushRequestDto(SendPushCodeRequestDto sendPushCodeRequestDto, String serviceTarget) {

        String regId = sendPushCodeRequestDto.getRegId();
        String pushType = sendPushCodeRequestDto.getPushType();
        String sendCode = sendPushCodeRequestDto.getSendCode();
        String regType = sendPushCodeRequestDto.getRegType();
        Map<String, String> paramMap = sendPushCodeRequestDto.getReserve();
        List items = sendPushCodeRequestDto.getItems();

        String paramList;
        String[] pushParamList;
        int paramSize =0;

        //입력받은 sendCode 를 이용해 푸시발송에 필요한 정보를 가져온다
        Map<String, String> pushInfoMap = sendPushCodeProps.findMapBySendCode(sendCode).orElseThrow(() -> new InvalidSendPushCodeException("send code 미지원"));
        //serviceType에 따라 다른 appId와 serviceId를 가져오며 serviceType이 빈 값이거나 H 일경우  default 값을 셋팅한다
        Map<String, String> appInfoDefaultMap = sendPushCodeProps.findMapByServiceType("default").orElseThrow();

        //serviceType에 따라 다른 appId와 serviceId를 가져온다
        Map<String, String> appInfoMap = sendPushCodeProps.findMapByServiceType(serviceTarget).orElse(Map.of());

        String serviceId = appInfoMap.get("gcm.serviceid");
        String appId = appInfoMap.get("gcm.appid");
        String payload = pushInfoMap.get("gcm.payload.body");
        String payloadItem = pushInfoMap.get("apns.payload.item");  //APNS전용 추가 item

        if (serviceTarget.equals("") || serviceTarget.equals("H")) {

            serviceId = appInfoDefaultMap.get("gcm.serviceid");
            appId = appInfoDefaultMap.get("gcm.appid");
        }

        log.debug("sendPushCtn Property Data Check : {} {} {}", serviceId, appId, payload);

        //reserve에 들어갈 내용을
        paramList = pushInfoMap.get("param.list");;
        pushParamList = paramList.split("\\|");
        paramSize = pushParamList.length;

        for (int index = 0; index < paramSize; index++) {
            try {
                payload = payload.replace("[+" + pushParamList[index] + "]", paramMap.get(pushParamList[index]));
            } catch (Exception e) {
                payload = payload.replace("[+" + pushParamList[index] + "]", "");
            }
        }

        if(pushType.equals("A")) {
            //APNS일 경우 items의 맨 앞에 payloaditem를 끼워 넣는다.
            items.add(0, payloadItem);
        }

        regId = getRegistrationID(sendPushCodeRequestDto);

        payload.replace("\"", "\\\"");

        return PushRequestSingleDto.builder()
                .appId(extraAppId)
                .serviceId(extraServiceId)
                .pushType("L")
                .msg(payload)
                .regId(regId)
                .items(items)
                .build();
    }
}
