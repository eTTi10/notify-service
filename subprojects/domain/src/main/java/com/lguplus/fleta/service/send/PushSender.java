package com.lguplus.fleta.service.send;

import com.lguplus.fleta.client.PersonalizationDomainClient;
import com.lguplus.fleta.client.SubscriberDomainClient;
import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.SaIdDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushRequestDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.PushServiceResultDto;
import com.lguplus.fleta.data.dto.response.SendPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException;
import com.lguplus.fleta.properties.SendPushCodeProps;
import com.lguplus.fleta.service.httppush.HttpSinglePushDomainService;
import com.lguplus.fleta.service.push.PushSingleDomainService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class PushSender {

    private static final String KEY_GCM_SERVICE_ID = "gcm.serviceid";
    private static final String KEY_GCM_APPID = "gcm.appid";
    private static final String KEY_GCM_PAYLOAD_BODY = "gcm.payload.body";
    private static final String KEY_PARAM_LIST = "param.list";
    private static final String KEY_APNS_SERVICE_ID = "apns.serviceid";
    private static final String KEY_APNS_APPID = "apns.appid";
    private static final String KEY_APNS_PAYLOAD_BODY = "apns.payload.body";
    private static final String KEY_APNS_PAYLOAD_ITEM = "apns.payload.item";
    private static final String MESSAGE_1001 = "Push GW Precondition Failed or Not Exist RegistID";
    private final HttpSinglePushDomainService httpSinglePushDomainService;
    private final PushSingleDomainService pushSingleDomainService;
    private final PersonalizationDomainClient personalizationDomainClient;
    private final SubscriberDomainClient subscriberDomainClient;
    private final SendPushCodeProps sendPushCodeProps;
    private final String fcmExtraSend;
    private final String extraServiceId;
    private final String extraApplicationId;
    private final List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();  //서비스타입별 결과 저장용 List
    private boolean successCheckFlag = false;
    private boolean check1001Flag = false;
    private boolean resultFlag = true;
    private String sFlag;
    private String sMessage;
    private int chk1001;
    private int failCount;
    private String failCode;
    private String failMessage;
    private String firstFailCode;
    private String firstFailMessage;

    /**
     * code를 이용한 push발송
     *
     * @param sendPushCodeRequestDto
     * @return
     */
    public SendPushResponseDto sendPushCode(SendPushCodeRequestDto sendPushCodeRequestDto) {
        String[] pushTypes = getServiceTypes(sendPushCodeRequestDto.getPushType(), "\\|");
        String[] serviceTypes = getServiceTypes(sendPushCodeRequestDto.getServiceType(), "\\|");

        initPushCodeValues();

        //입력받은 sendCode 를 이용해 푸시발송에 필요한 정보를 가져온다

        Map<String, String> sendCodeMap = getSendCodeMap(sendPushCodeRequestDto.getSendCode());

        //invalid sendCode 체크해서 Exception
        checkGCMBody(sendCodeMap);

        /*FCM POS 추가발송 설정값 체크*/
        String extraSendYn = StringUtils.defaultIfEmpty(sendCodeMap.get("pos.send"), fcmExtraSend);
        log.debug("pushInfoMap.getpos.send props:" + sendCodeMap.get("pos.send"));
        log.debug("fcmExtraSend props:" + fcmExtraSend);

        for (String serviceType : serviceTypes) {
            initPushValues();
            for (String type : pushTypes) {
                HttpPushSingleRequestDto httpPushSingleRequestDto = setHttpPushRequestDto(sendPushCodeRequestDto, serviceType, type);
                log.debug("httpPushSingleRequestDto:{}", httpPushSingleRequestDto);
                setPushResult(requestHttpSinglePush(httpPushSingleRequestDto));
                //푸시의 대상타입이 U+tv이며 sendCode에 대한 property가 추가발송에 해당하는 경우
                sendExtraPush(sendPushCodeRequestDto, type, serviceType, extraSendYn);
            } // pushType for end
            String sType = StringUtils.defaultIfEmpty(serviceType, "H");
            //serviceType별 성공실패 기록
            setServiceResult(pushTypes.length);

            firstFailCode = StringUtils.defaultIfEmpty(firstFailCode, failCode);
            firstFailMessage = StringUtils.defaultIfEmpty(firstFailMessage, failMessage);

            PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder().type(sType).flag(sFlag).message(sMessage).build();
            pushServiceResultDtoArrayList.add(pushServiceResultDto);
        }    // serviceType for end

        //response
        return setResponseDto(serviceTypes.length);
    }

    private HttpPushResponseDto requestHttpSinglePush(HttpPushSingleRequestDto httpPushSingleRequestDto) {
        HttpPushResponseDto httpPushResponseDto = null;
        try {
            httpPushResponseDto = httpSinglePushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);
        } catch (HttpPushCustomException ex) {
            log.debug("code :::::::::::: {}\tmessage :::::::::::::::: {}", ex.getCode(), ex.getMessage());
            failCode = ex.getCode();
            failMessage = ex.getMessage();
        }
        if (httpPushResponseDto != null && !"성공".equals(httpPushResponseDto.getMessage())) {
            failCode = httpPushResponseDto.getCode();
            failMessage = httpPushResponseDto.getMessage();
        }
        return httpPushResponseDto;
    }

    private String[] getServiceTypes(String serviceType, String regex) {
        return serviceType.split(regex);
    }

    private Map<String, String> getSendCodeMap(String sendCode) {
        return sendPushCodeProps.findMapBySendCode(sendCode).orElse(Map.of());
    }

    private void sendExtraPush(SendPushCodeRequestDto sendPushCodeRequestDto, String pushType, String serviceTarget, String extraSendYn) {
        if (serviceTarget.equals("TV") && extraSendYn.equals("Y")) {

            PushRequestSingleDto pushRequestSingleDto = getExtraPushRequestDto(sendPushCodeRequestDto, serviceTarget, pushType);

            log.debug("PushRequestSingleDto:{}", pushRequestSingleDto);

            try {

                //소켓 PUSH 호출
                PushClientResponseDto pushClientResponseDto = pushSingleDomainService.requestPushSingle(pushRequestSingleDto);

                log.debug("pushClientResponseDto:" + pushClientResponseDto);

            } catch (NotifyRuntimeException ne) {
                log.debug("NotifyPushRuntimeException:{} {}", ne, ne.getInnerResponseCodeType());
            }
        }
    }

    private void initPushCodeValues() {
        resultFlag = true;
        chk1001 = 0;
        failCount = 0;
        failCode = "";
        failMessage = "";

        firstFailCode = "";
        firstFailMessage = "";

        pushServiceResultDtoArrayList.clear();

    }

    private void initPushValues() {
        sFlag = "";
        sMessage = "";

        chk1001 = 0;
        failCount = 0;

        failCode = "";
        failMessage = "";
    }

    /**
     * pushType별 HTTPPUSH용 requestDto 조립
     *
     * @param sendPushCodeRequestDto
     * @param serviceType
     * @param pushType
     * @return
     */
    private HttpPushSingleRequestDto setHttpPushRequestDto(SendPushCodeRequestDto sendPushCodeRequestDto, String serviceType, String pushType) {
        if (isGcmTypeTvTarget(serviceType, pushType)) {   //GCM 이거나 푸시의 대상타입이 U+tv 일 경우
            return getGcmOrTVRequestDto(sendPushCodeRequestDto, serviceType, pushType);
        } else if (pushType.equalsIgnoreCase("A")) {  //APNS 일경우
            return getApnsRequestDto(sendPushCodeRequestDto, serviceType, pushType);
        } else if (pushType.equalsIgnoreCase("L")) {    // LG 푸시 일 경우
            return getPosRequestDto(sendPushCodeRequestDto, serviceType, pushType);
        }
        return null;
    }


    /**
     * pushType별 failCode 기록 로직
     *
     * @param httpPushResponseDto
     */
    private void setPushResult(HttpPushResponseDto httpPushResponseDto) {

        if (httpPushResponseDto == null || !httpPushResponseDto.getMessage().equals("성공")) { // 실패일 경우 처리

            if (failCode.equals("1113") || failCode.equals("1108")) {
                chk1001++;
            } else {
                resultFlag = false;
            }

            failCount++;
        }

    }

    /**
     * 성공, 실패코드 response 로직
     *
     * @param serviceTypeSize
     * @return
     */
    private SendPushResponseDto setResponseDto(int serviceTypeSize) {
        //성공일 경우
        if (successCheckFlag) {
            return SendPushResponseDto.builder().flag("0000").message("성공").service(pushServiceResultDtoArrayList).build();
        } else {
            if (check1001Flag && serviceTypeSize > 1) {
                return SendPushResponseDto.builder().flag("1001").message(MESSAGE_1001).service(pushServiceResultDtoArrayList).build();
            } else {
                return SendPushResponseDto.builder().flag(firstFailCode).message(firstFailMessage).service(pushServiceResultDtoArrayList).build();
            }
        }
    }

    /**
     * RegId 조회
     *
     * @param sendPushCodeRequestDto
     * @return
     */
    public String getRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto) {

        Map<String, String> inputMap = new HashMap<>();

        inputMap.put("saId", sendPushCodeRequestDto.getSaId());
        inputMap.put("stbMac", sendPushCodeRequestDto.getStbMac());

        RegIdDto regIdDto = Optional.ofNullable(personalizationDomainClient.getRegistrationID(inputMap)).orElse(RegIdDto.builder().registrationId("").build());

        log.debug("personalizationDomainClient.getRegistrationID() regIdDto:{}", regIdDto);

        return regIdDto.getRegistrationId();

    }

    /**
     * ctn을 이용해 registrationId 조회
     *
     * @param ctn
     * @return
     */
    public String getRegistrationIDbyCtn(String ctn) {

        Map<String, String> inputMap = new HashMap<>();

        ctn = "0" + ctn;
        inputMap.put("ctnNo", ctn);

        List<SaIdDto> saIdDtoList = Optional.ofNullable(subscriberDomainClient.getRegistrationIDbyCtn(inputMap)).orElseThrow();
        log.debug("subscriberDomainClient.getRegistrationIDbyCtn() saIdDtoList:{}", saIdDtoList);
        if (!saIdDtoList.isEmpty()) {
            return StringUtils.defaultIfEmpty(saIdDtoList.get(0).getSaId(), "");
        } else {
            return "";
        }

    }

    /**
     * GCM(pushType) or U+tv(serviceType)용 request dto 조립
     *
     * @param sendPushCodeRequestDto
     * @param serviceType
     * @return
     */
    public HttpPushSingleRequestDto getGcmOrTVRequestDto(SendPushCodeRequestDto sendPushCodeRequestDto, String serviceType, String pushType) {

        String registrationId = sendPushCodeRequestDto.getRegistrationId();
        String sendCode = sendPushCodeRequestDto.getSendCode();
        String regType = sendPushCodeRequestDto.getRegType();
        List<String> items = sendPushCodeRequestDto.getItems();

        Map<String, String> paramMap = sendPushCodeRequestDto.getReserve();
        log.debug("sendPushCodeRequestDto.getSendCode() : {}", sendPushCodeRequestDto.getSendCode());
        log.debug("sendPushCodeRequestDto.getReserve() : {}", sendPushCodeRequestDto.getReserve());
        log.debug("paramMap : {}", paramMap);
        //serviceType에 따라 다른 appId와 serviceId를 가져오는데, serviceType이 빈 값이거나 H 일경우  default 값을 셋팅한다

        log.debug("sendCode : {}", sendCode);

        //입력받은 sendCode 를 이용해 푸시발송에 필요한 정보를 가져온다
        Map<String, String> pushInfoMap = sendPushCodeProps.findMapBySendCode(sendCode).orElse(Map.of());
        checkGCMBody(pushInfoMap);

        String payload = pushInfoMap.get(KEY_GCM_PAYLOAD_BODY);
        String payloadItem = pushInfoMap.get(KEY_APNS_PAYLOAD_ITEM);  //APNS전용 추가 item


        log.debug("sendPushCtn  : {}", payload);

        //reserve에 들어갈 내용을
        String params = pushInfoMap.get(KEY_PARAM_LIST);
        String[] pushParams = params.split("\\|");
        int paramSize = pushParams.length;

        payload = replacePayload(paramMap, pushParams, paramSize, payload);

        registrationId = (regType.equals("2")) ? getRegistrationIDbyCtn(registrationId) : registrationId;

        if (pushType.equals("A")) {
            payloadItem = replacePayload(paramMap, pushParams, paramSize, payloadItem);

            //APNS일 경우 items의 맨 앞에 payloaditem를 끼워 넣는다.
            items.add(0, payloadItem);
        }

        log.debug("sendPushCtn  : {}",payload);

        return HttpPushSingleRequestDto.builder()
            .applicationId(getApplicationId(serviceType,pushType))
            .serviceId(getServiceId(serviceType,pushType))
            .pushType(pushType)
            .message(payload)
            .users(List.of(registrationId))
            .items(items)
            .build();
    }

    /**
     * APNS용 request dto 조립
     *
     * @param sendPushCodeRequestDto
     * @param serviceType
     * @return
     */
    public HttpPushSingleRequestDto getApnsRequestDto(SendPushCodeRequestDto sendPushCodeRequestDto, String serviceType, String pushType) {

        String registrationId = sendPushCodeRequestDto.getRegistrationId();
        String sendCode = sendPushCodeRequestDto.getSendCode();
        String regType = sendPushCodeRequestDto.getRegType();
        List<String> items = sendPushCodeRequestDto.getItems();
        Map<String, String> paramMap = sendPushCodeRequestDto.getReserve();
        log.debug("sendPushCodeRequestDto.getReserve() : {}", sendPushCodeRequestDto.getReserve());
        log.debug("paramMap : {}", paramMap);

        //입력받은 sendCode 를 이용해 푸시발송에 필요한 정보를 가져온다
        Map<String, String> pushInfoMap = sendPushCodeProps.findMapBySendCode(sendCode).orElse(Map.of());

        checkGCMBody(pushInfoMap);


        String serviceId = getServiceId(serviceType,pushType);
        String applicationId = getApplicationId(serviceType,pushType);
        String payload = pushInfoMap.get(KEY_APNS_PAYLOAD_BODY);
        String payloadItem = pushInfoMap.get(KEY_APNS_PAYLOAD_ITEM);  //APNS전용 추가 item

        //reserve에 들어갈 내용을
        String paramList = pushInfoMap.get(KEY_PARAM_LIST);
        String[] pushParamList = getServiceTypes(paramList, "\\|");
        int paramSize = pushParamList.length;

        log.debug("pushinfo.{}.param.list : {}", sendCode, pushParamList);

        log.debug("sendPushCtn Property Data Check : {} {} {} {}", serviceId, applicationId, payload, payloadItem);

        payloadItem = replacePayload(paramMap, pushParamList, paramSize, payloadItem);

        registrationId = (regType.equals("2")) ? getRegistrationIDbyCtn(registrationId) : registrationId;

        //APNS 일 경우 items 의 맨 앞에 payloaditem 를 끼워 넣는다.
        items.add(0, payloadItem);

        return HttpPushSingleRequestDto.builder()
            .applicationId(applicationId)
            .serviceId(serviceId)
            .pushType(pushType)
            .message(payload)
            .users(List.of(registrationId))
            .items(items)
            .build();
    }

    /**
     * LG 푸시용 request dto 조립 ( pushAgent_op 에서는 push_type 이 GCM 과 APNS 만 사용할 수 있지만 ASIS 에 있는 로직이라 개발함
     *
     * @param sendPushCodeRequestDto
     * @param serviceType
     * @return
     */
    public HttpPushSingleRequestDto getPosRequestDto(SendPushCodeRequestDto sendPushCodeRequestDto, String serviceType, String pushType) {

        String registrationId = sendPushCodeRequestDto.getRegistrationId();
        List<String> items = sendPushCodeRequestDto.getItems();
        String bodyLgPush = sendPushCodeRequestDto.getRequestBodyStr();

        String serviceId = getServiceId(serviceType,pushType);
        String applicationId = getApplicationId(serviceType,pushType);

        //reg_id를 기입하지 않았다면 DB 에서 RegID를 찾아서 처리한다.
        registrationId = StringUtils.defaultIfEmpty(registrationId, getRegistrationID(sendPushCodeRequestDto));
        return HttpPushSingleRequestDto.builder().applicationId(applicationId).serviceId(serviceId).pushType(pushType).message(bodyLgPush).users(List.of(registrationId)).items(items).build();
    }

    private String getServiceId(String serviceTarget, String pushType){
        Map<String, String> appInfoDefaultMap = getAppDefaultMap();
        Map<String, String> appInfoMap = sendPushCodeProps.findMapByServiceType(serviceTarget).orElse(Map.of());
        if (isGcmTypeTvTarget(serviceTarget, pushType)) {
            return (serviceTarget.equals("") || serviceTarget.equals("H")) ? appInfoDefaultMap.get(KEY_GCM_SERVICE_ID) : appInfoMap.get(KEY_GCM_SERVICE_ID);
        } else if (pushType.equalsIgnoreCase("A")) {
            String serviceId = appInfoMap.get(KEY_APNS_SERVICE_ID);

            if (serviceTarget.equals("") || serviceTarget.equals("H")) {
                serviceId = appInfoDefaultMap.get(KEY_APNS_SERVICE_ID);
            }
            return serviceId;
        } else if (pushType.equalsIgnoreCase("L")) {
            return Optional.ofNullable(appInfoMap.get("pos.serviceid")).orElseThrow(() -> new InvalidSendPushCodeException("LG Push 미지원"));
        }
        return "";
    }

    private boolean isGcmTypeTvTarget(String serviceTarget, String pushType) {
        return pushType.equalsIgnoreCase("G") || serviceTarget.equalsIgnoreCase("TV");
    }

    private String getApplicationId(String serviceTarget, String pushType) {
        Map<String, String> appInfoDefaultMap = getAppDefaultMap();
        Map<String, String> appInfoMap = sendPushCodeProps.findMapByServiceType(serviceTarget).orElse(Map.of());
        if (isGcmTypeTvTarget(serviceTarget, pushType)) {
            String applicationId = appInfoMap.get(KEY_GCM_APPID);
            if (serviceTarget.equals("") || serviceTarget.equals("H")) {
                applicationId = appInfoDefaultMap.get(KEY_GCM_APPID);
            }
            return applicationId;
        } else if (pushType.equalsIgnoreCase("A")) {
            String applicationId = appInfoMap.get(KEY_APNS_APPID);

            if (serviceTarget.equals("") || serviceTarget.equals("H")) {
                applicationId = appInfoDefaultMap.get(KEY_APNS_APPID);
            }
            return applicationId;
        } else if (pushType.equalsIgnoreCase("L")) {
            return Optional.ofNullable(appInfoMap.get("pos.appid")).orElseThrow(() -> new InvalidSendPushCodeException("LG Push 미지원"));

        }
        return "";
    }

    /**
     * 소켓 푸시 전문 조립
     *
     * @param sendPushCodeRequestDto
     * @param serviceTarget
     * @return
     */
    public PushRequestSingleDto getExtraPushRequestDto(SendPushCodeRequestDto sendPushCodeRequestDto, String serviceTarget, String pushType) {

        String registrationId = getRegistrationID(sendPushCodeRequestDto);
        String sendCode = sendPushCodeRequestDto.getSendCode();
        Map<String, String> paramMap = sendPushCodeRequestDto.getReserve();
        List<String> items = sendPushCodeRequestDto.getItems();
        //입력받은 sendCode 를 이용해 푸시발송에 필요한 정보를 가져온다
        Map<String, String> pushInfoMap = sendPushCodeProps.findMapBySendCode(sendCode).orElse(Map.of());

        checkGCMBody(pushInfoMap);

        String serviceId = getServiceId(serviceTarget,pushType);
        String applicationId = getApplicationId(serviceTarget,pushType);
        String payload = pushInfoMap.get(KEY_GCM_PAYLOAD_BODY);
        String payloadItem = pushInfoMap.get(KEY_APNS_PAYLOAD_ITEM);  //APNS 전용 추가 item

        log.debug("sendPushCtn Property Data Check : {} {} {}", serviceId, applicationId, payload);

        //reserve에 들어갈 내용을
        String paramList = pushInfoMap.get(KEY_PARAM_LIST);
        String[] pushParams = paramList.split("\\|");
        int paramSize = pushParams.length;

        payload = replacePayload(paramMap, pushParams, paramSize, payload);

        if (pushType.equals("A")) {
            payloadItem = replacePayload(paramMap, pushParams, paramSize, payloadItem);

            //APNS 일 경우 items 의 맨 앞에 payloadItem 을 끼워 넣는다.
            items.add(0, payloadItem);
        }

        //소켓푸시용 items로 변환
        List<PushRequestItemDto> itemsExtra = new ArrayList<>();
        items.forEach(e -> {
            String[] parseItems = getServiceTypes(e, "!\\^");
            if (parseItems.length == 2) {
                itemsExtra.add(PushRequestItemDto.builder().itemKey(parseItems[0]).itemValue(parseItems[1]).build());
            } else {
                log.error(" items error");
            }
        });

        return PushRequestSingleDto.builder().applicationId(extraApplicationId).serviceId(extraServiceId).pushType("L").message(payload).regId(registrationId).items(itemsExtra).build();
    }


    /**
     * httpSingle 푸시 전문 조립
     */
    public HttpPushSingleRequestDto getPushRequestDto(HttpPushRequestDto httpPushDeviceRequestDto, String agentType){
        Map<String, String> paramMap = httpPushDeviceRequestDto.getReserve();
        String applicationId = "";
        String serviceId = "";
        String payload = "";
        List<String> items = httpPushDeviceRequestDto.getItems();

        Map<String, String> mapBySendCode = sendPushCodeProps.findMapBySendCode(httpPushDeviceRequestDto.getSendCode()).orElse(Map.of());
        checkGCMBody(mapBySendCode);
        String paramList = mapBySendCode.get(KEY_PARAM_LIST);
        String[] pushParams = paramList.split("\\|");
        int paramSize = pushParams.length;

        //serviceType에 따라 다른 appId와 serviceId를 가져온다
        String serviceType = httpPushDeviceRequestDto.getServiceType().equals("H") ? "default" : httpPushDeviceRequestDto.getServiceType();
        Map<String, String> mapByApplicationInfo = sendPushCodeProps.findMapByServiceType(serviceType).orElse(Map.of());

        if(agentType.equals("G")){
            payload = mapBySendCode.get(KEY_GCM_PAYLOAD_BODY);
            payload = replacePayload(paramMap, pushParams, paramSize, payload);
            applicationId = mapByApplicationInfo.get(KEY_GCM_APPID);
            serviceId = mapByApplicationInfo.get(KEY_GCM_SERVICE_ID);
        }else if(agentType.equals("A")){
            payload = mapBySendCode.get(KEY_APNS_PAYLOAD_BODY);
            String payloadItem = mapBySendCode.get(KEY_APNS_PAYLOAD_ITEM);
            payloadItem = replacePayload(paramMap, pushParams, paramSize, payloadItem);

//            APNS 일 경우 items 의 맨 앞에 payloadItem 을 끼워 넣는다.
            items.add(0, payloadItem);

            applicationId = mapByApplicationInfo.get(KEY_APNS_APPID);
            serviceId = mapByApplicationInfo.get(KEY_APNS_SERVICE_ID);
        }

        return HttpPushSingleRequestDto.builder()
                .applicationId(applicationId)
                .serviceId(serviceId)
                .pushType(agentType)
                .message(payload)
                .items(items)
                .users(List.of(httpPushDeviceRequestDto.getSaId()))
                .build();
    }

    private String replacePayload(Map<String, String> paramMap, String[] pushParams, int paramSize, String payload) {
        for (int index = 0; index < paramSize; index++) {
            try {
                payload = payload.replace("[+" + pushParams[index] + "]", paramMap.get(pushParams[index]));
            } catch (Exception e) {
                payload = payload.replace("[+" + pushParams[index] + "]", "");
            }
        }
        return payload;
    }



    /**
     * sendCode 체크
     *
     * @param pushInfoMap
     */
    public void checkGCMBody(Map<String, String> pushInfoMap) {

        if (!pushInfoMap.containsKey(KEY_GCM_PAYLOAD_BODY) || pushInfoMap.get(KEY_GCM_PAYLOAD_BODY).equals("")) {
            throw new InvalidSendPushCodeException("send code 미지원");
        }
    }


    /**
     * default serviceType property값 조회
     *
     * @return Map
     */
    private Map<String, String> getAppDefaultMap() {

        //serviceType 에 따라 다른 appId와 serviceId를 가져오는데, serviceType이 빈 값이거나 H 일경우  default 값을 셋팅한다
        return sendPushCodeProps.findMapByServiceType("default").orElseThrow();
    }

    /**
     * serviceType별 성공실패 기록
     *
     * @param pushTypeSize
     */
    private void setServiceResult(int pushTypeSize) {

        if (failCount < 2 && pushTypeSize > 1) { //푸시타입이 2개 이상인 경우 실패건수가 1건 이하이면 성공으로 간주
            sFlag = "0000";
            sMessage = "성공";
            successCheckFlag = true;
        } else if (!resultFlag && pushTypeSize > 1) {  // 푸시 타입이 2개 이상이며 1001이 아닌 오류가 났을 경우 해당 오류코드를 반환
            sFlag = failCode;
            sMessage = failMessage;
        } else {
            if (failCount == 0) {    // 실패건수가 0일 경우 성공 반환
                sFlag = "0000";
                sMessage = "성공";
                successCheckFlag = true;
            } else if (chk1001 > 0) { // 1001 오류가 1건 이상일 경우 1001 반환
                sFlag = "1001";
                sMessage = MESSAGE_1001;
                check1001Flag = true;
            } else {    // 기타 해당 오류 코드 반환
                sFlag = failCode;
                sMessage = failMessage;
            }
        }


    }

}
