package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.httppush.InvalidSendPushCodeException;
import com.lguplus.fleta.properties.SendPushCodeProps;
import com.lguplus.fleta.service.httppush.HttpPushDomainService;
import com.lguplus.fleta.service.push.PushSingleDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {

    @Value("${fcm.extra.target}")
    private String extraTarget;

    @Value("${fcm.extra.serviceid}")
    private String extraServiceId;

    @Value("${fcm.extra.appid}")
    private String extraAppId;

    @Value("${fcm.extra.send}")
    private String fcmExtraSend;

    private final PushDomainService pushDomainService;
    private final HttpPushDomainService httpPushDomainService;
    private final PushSingleDomainService pushSingleDomainService;
    private final SendPushCodeProps sendPushCodeProps;

    public SuccessResponseDto sendPushCode(SendPushCodeRequestDto sendPushCodeRequestDto) {

        String saId = sendPushCodeRequestDto.getSendCode();
        String stbMac = sendPushCodeRequestDto.getStbMac();
        String regId = sendPushCodeRequestDto.getRegId();
        String pushType = sendPushCodeRequestDto.getPushType();
        String sendCode = sendPushCodeRequestDto.getSendCode();
        String regType = sendPushCodeRequestDto.getRegType();
        String serviceType = sendPushCodeRequestDto.getServiceType();
        List items = sendPushCodeRequestDto.getItems();
        String[]  pushTypeList;
        String[]  serviceTypeList;
        String[]  pushParamList;
        Map<String, String> paramMap = new HashMap<String, String>();
        String msg = "";
        String payload ="";
        String payloadItem ="";
        String serviceId  ="";
        String appId = "";
        String result = null;
        Boolean resultCode = true;
        String pushParam ="";
        String failcode = "";
        int chk1001 =0;
        int failCount =0;
        int paramSize =0;
        String failMessage ="";
        boolean SuccessCheckFlag = false;
        boolean check1001falg = false;
        String firstFailcode ="";
        String firstFailmessage ="";

        boolean resultFcm = false;
        boolean resultPos = false;
        String payloadPos ="";

        //입력받은 sendCode 를 이용해 푸시발송에 필요한 정보를 가져온다
        Map<String, String> pushInfoMap = sendPushCodeProps.findMapBySendCode(sendCode).orElseThrow(() -> new InvalidSendPushCodeException("send code 미지원"));
        //serviceType에 따라 다른 appId와 serviceId를 가져오며 serviceType이 빈 값이거나 H 일경우  default 값을 셋팅한다
        Map<String, String> appInfoDefaultMap = sendPushCodeProps.findMapByServiceType("default").orElseThrow();
        Map<String, String> appInfoMap;

        //msg 내용 가져오기
        String gcmPayLoadBody = pushInfoMap.get("gcm.payload.body");
        log.debug("pushinfo.{}.gcm.payload.body : {}", sendCode, gcmPayLoadBody);

        //reserve에 들어갈 내용을
        String paramList = pushInfoMap.get("param.list");;
        pushParamList = paramList.split("\\|");
        paramSize = pushParamList.length;

        log.debug("pushinfo.{}.param.list : {}", sendCode, pushParamList);

        if(sendCode.substring(0,1).equals("T")){
            pushType = "G";
        }


        pushTypeList = pushType.split("\\|");
        serviceTypeList = serviceType.split("\\|");

        /*FCM POS 추가발송 설정값 체크*/
        String extraSendYn = StringUtils.defaultIfEmpty(pushInfoMap.get("pos.send"), fcmExtraSend);

        for(int k=0; k<serviceTypeList.length; k++) {

            String serviceTarget = serviceTypeList[k];

            log.debug("serviceTarget: {} ", serviceTarget);

            //serviceType에 따라 다른 appId와 serviceId를 가져온다
            appInfoMap = sendPushCodeProps.findMapByServiceType(serviceTarget).orElse(appInfoDefaultMap);

            chk1001 = 0;
            failCount = 0;

            for(int i=0; i<pushTypeList.length; i++){

                pushType = pushTypeList[i];

                //GCM 이거나 푸시의 대상타입이 U+tv 일 경우
                if(pushType.equals("G") || serviceTarget.equals("TV")) {

                    serviceId = appInfoMap.get("gcm.serviceid");
                    appId = appInfoMap.get("gcm.appid");
                    payload = pushInfoMap.get("gcm.payload.body");

                    if(serviceTarget.equals("") || serviceTarget.equals("H")){

                        serviceId = appInfoDefaultMap.get("gcm.serviceid");
                        appId = appInfoDefaultMap.get("gcm.appid");
                    }

                    log.debug("sendPushCtn Property Data Check : {} {} {}", serviceId , appId , payload);

                    for(int index=0; index<paramSize; index++){
                        try {
                            payload = payload.replace("[+"+pushParamList[index]+"]", paramMap.get(pushParamList[index]));
                        } catch(Exception e){
                            payload = payload.replace("[+"+pushParamList[index]+"]","");
                        }
                    }

                    if(regType.equals("2")){
                        String ctn = "0" + regId;
                        regId = pushDomainService.getRegistrationIDbyCtn(ctn);
//                        regId = "M00020200205"; // TODO 실제 Feiin 연결 후 삭제
                    }


                //APNS 일경우
                } else if(pushType.equals("A")) {

                    serviceId = appInfoMap.get("apns.serviceid");
                    appId = appInfoMap.get("apns.appid");
                    payload = pushInfoMap.get("apns.payload.body");
                    payloadItem = pushInfoMap.get("apns.payload.item");


                    if(serviceTarget.equals("") || serviceTarget.equals("H")){
                        serviceId = appInfoDefaultMap.get("apns.serviceid");
                        appId = appInfoDefaultMap.get("apns.appid");
                    }

                    log.debug("sendPushCtn Property Data Check : {} {} {}", serviceId , appId , payload, payloadItem);

                    for(int index=0; index<paramSize; index++){
                        try {
                            payloadItem = payloadItem.replace("[+"+pushParamList[index]+"]", paramMap.get(pushParamList[index]));
                        } catch(Exception e){
                            payloadItem = payloadItem.replace("[+"+pushParamList[index]+"]", "");
                        }
                    }


                    if(regType.equals("2")){
                        String ctn = "0" + regId;
                        regId = pushDomainService.getRegistrationIDbyCtn(ctn);
//                        regId = "M00020200205"; // TODO 실제 Feiin 연결 후 삭제
                    }

                    //APNS일 경우 items의 맨 앞에 payloaditem를 끼워 넣는다.
                    items.add(0, payloadItem);

                // LG 푸시 일 경우
                } else if(pushType.equalsIgnoreCase("L")){

                    serviceId = Optional.of(appInfoMap.get("pos.serviceid")).orElseThrow(() -> new InvalidSendPushCodeException("LG Push 미지원"));
                    appId = Optional.of(appInfoMap.get("pos.appid")).orElseThrow(() -> new InvalidSendPushCodeException("LG Push 미지원"));

                    //reg_id를 기입하지 않았다면 DB에서 RegID를 찾아서 처리한다.
                    if(StringUtils.isEmpty(regId)){

                        regId = pushDomainService.getRegistrationID(sendPushCodeRequestDto);
//                        regId = "M00020200205"; // TODO 실제 Feiin 연결 후 삭제


                    }

                }

                log.debug("sendPushCtn payload : {} {} {}", payload, payloadItem, regId);

                //HTTP PUSH 호출
                payload.replace("\"", "\\\"");

                List<String> users = new ArrayList<>();
                users.add(regId);

                HttpPushSingleRequestDto httpPushSingleRequestDto = HttpPushSingleRequestDto.builder()
                        .appId(appId)
                        .serviceId(serviceId)
                        .pushType(pushType)
                        .msg(payload)
                        .users(users)
                        .items(items)
                        .build();

                HttpPushResponseDto httpPushResponseDto =  httpPushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);

                if (serviceTarget.equals("TV") && httpPushResponseDto.getCode().equals("200")) {
                    //"성공"
                    resultFcm = true;
                }
                else {
                    throw new NotifyHttpPushRuntimeException();
                }

                //추가 발송 셋팅 (소켓)
                //푸시의 대상타입이 U+tv이고  send_code에 대한 설정값이 추가발송에 해당하는 경우
                if(serviceTarget.equals("TV") && extraSendYn.equals("Y") ){

                    regId = pushDomainService.getRegistrationID(sendPushCodeRequestDto);
//                        regId = "M00020200205"; // TODO 실제 Feiin 연결 후 삭제

                    payloadPos.replace("\"", "\\\"");

                    //소켓 PUSH 호출
                    PushRequestSingleDto pushRequestSingleDto = PushRequestSingleDto.builder()
                            .appId(extraAppId)
                            .serviceId(extraServiceId)
                            .pushType("L")
                            .msg(payload)
                            .regId(regId)
                            .items(items)
                            .build();

                    PushClientResponseDto pushClientResponseDto =  pushSingleDomainService.requestPushSingle(pushRequestSingleDto);

                    if (pushClientResponseDto.getCode().equals("200")) {
                        //"성공"
                        resultPos = true;
                    }


                    log.debug("extra sendPush Request POS : {} {} {} {}", saId, stbMac, pushClientResponseDto.getCode(), pushClientResponseDto.getMessage());

                }



                log.debug("sendPushCtn Request : {} {} {} {} {}", saId, stbMac, httpPushResponseDto.getCode(), httpPushResponseDto.getMessage(), httpPushResponseDto.getFailUsers());


            } // pushType for end






        }	// serviceType for end


        // TODO 성공 조건 로직 구성해야 함
        if (resultFcm == true) {
            return SuccessResponseDto.builder().build();
        }
        else {
            throw new NotifyPushRuntimeException();
        }

    }


}
