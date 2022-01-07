package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.PushServiceResultDto;
import com.lguplus.fleta.data.dto.response.SendPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;
import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import com.lguplus.fleta.properties.SendPushCodeProps;
import com.lguplus.fleta.service.httppush.HttpSinglePushDomainService;
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

    @Value("${fcm.extra.send}")
    private String fcmExtraSend;

    private final PushDomainService pushDomainService;
    private final HttpSinglePushDomainService httpSinglePushDomainService;
    private final PushSingleDomainService pushSingleDomainService;
    private final SendPushCodeProps sendPushCodeProps;

    public SendPushResponseDto sendPushCode(SendPushCodeRequestDto sendPushCodeRequestDto) {

        HttpPushResponseDto httpPushResponseDto = null;

        String pushType = sendPushCodeRequestDto.getPushType();
        String sendCode = sendPushCodeRequestDto.getSendCode();
        String serviceType = sendPushCodeRequestDto.getServiceType();

        String[]  pushTypeList;
        String[]  serviceTypeList;
        String serviceTarget;
        Boolean resultFlag = true;

        int chk1001 =0;
        int failCount =0;
        boolean SuccessCheckFlag = false;
        boolean check1001Flag = false;
        String failCode = "";
        String failMessage ="";
        String firstFailCode ="";
        String firstFailMessage ="";

        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();  //서비스타입별 결과 저장용 List

        //입력받은 sendCode 를 이용해 푸시발송에 필요한 정보를 가져온다
        Map<String, String> pushInfoMap = sendPushCodeProps.findMapBySendCode(sendCode).orElse(Map.of());

        pushDomainService.checkInvalidSendPushCode(pushInfoMap);

        /*FCM POS 추가발송 설정값 체크*/
        String extraSendYn = StringUtils.defaultIfEmpty(pushInfoMap.get("pos.send"), fcmExtraSend);
        log.debug("pushInfoMap.getpos.send" + pushInfoMap.get("pos.send"));
        log.debug("fcmExtraSend" + fcmExtraSend);


        if(sendCode.substring(0,1).equals("T")){
            pushType = "G";
        }

        pushTypeList = pushType.split("\\|");
        serviceTypeList = serviceType.split("\\|");

        for(int k=0; k<serviceTypeList.length; k++) {

            String sType = "";
            String sFlag;
            String sMessage;
            failCode = "";
            failMessage ="";

            serviceTarget = serviceTypeList[k];

            log.debug("serviceTarget: {} ", serviceTarget);

            chk1001 = 0;
            failCount = 0;

            for(int i=0; i<pushTypeList.length; i++) {

                HttpPushSingleRequestDto httpPushSingleRequestDto = null;
                PushRequestSingleDto pushRequestSingleDto = null;

                if (pushTypeList[i].equalsIgnoreCase("G") || serviceTarget.equalsIgnoreCase("TV")) {   //GCM 이거나 푸시의 대상타입이 U+tv 일 경우

                    httpPushSingleRequestDto = pushDomainService.getGcmOrTVRequestDto(sendPushCodeRequestDto, serviceTarget);

                } else if (pushTypeList[i].equalsIgnoreCase("A")) {  //APNS 일경우

                    httpPushSingleRequestDto = pushDomainService.getApnsRequestDto(sendPushCodeRequestDto, serviceTarget);


                } else if (pushTypeList[i].equalsIgnoreCase("L")) {    // LG 푸시 일 경우

                    httpPushSingleRequestDto = pushDomainService.getPosRequestDto(sendPushCodeRequestDto, serviceTarget);
                }

                try {

                    //HTTP PUSH 호출
                    httpPushResponseDto = httpSinglePushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);

                } catch (HttpPushCustomException ex) {
                    log.debug("code :::::::::::: {}\tmessage :::::::::::::::: {}", ex.getCode(), ex.getMessage());
                    failCode = ex.getCode();
                    failMessage = ex.getMessage();
                }

                //추가 발송 셋팅 (소켓)
                //푸시의 대상타입이 U+tv이고  send_code에 대한 설정값이 추가발송에 해당하는 경우
                if (serviceTarget.equals("TV") && extraSendYn.equals("Y")) {

                    pushRequestSingleDto = pushDomainService.getExtraPushRequestDto(sendPushCodeRequestDto, serviceTarget);

                    log.debug("PushRequestSingleDto:{}", pushRequestSingleDto);

                    try {

                        //소켓 PUSH 호출
                        PushClientResponseDto pushClientResponseDto = pushSingleDomainService.requestPushSingle(pushRequestSingleDto);

                        log.debug("pushClientResponseDto:" + pushClientResponseDto);

                    } catch (NotifyPushRuntimeException ne) {
                        log.debug("NotifyPushRuntimeException:{} {}", ne.toString(), ne.getInnerResponseCodeType());
                    }

                }


                // failCode 기록 로직 시작
                if (httpPushResponseDto == null || ! httpPushResponseDto.getMessage().equals("성공") ) { // 실패일 경우 처리

                    if (failCode.equals("1113") || failCode.equals("1108")) {
                        chk1001++;
                    } else {
                        resultFlag = false;
                    }

                    failCount++;
                }

            } // pushType for end


            sType = StringUtils.defaultIfEmpty(serviceTarget, "H");
            //서비스별 성공실패 기록 로직
            if(chk1001 > 1 && pushTypeList.length > 1) {
                sFlag = "1001";
                sMessage = "Push GW Precondition Failed or Not Exist RegistID";
                check1001Flag = true;
            } else if( failCount < 2 && pushTypeList.length > 1){ //푸시타입이 2개 이상이고 실패건수가 1이하일때
                sFlag = "0000";
                sMessage = "성공";
                SuccessCheckFlag = true;
            } else if( resultFlag == false && pushTypeList.length > 1 ) {
                sFlag = failCode;
                sMessage = failMessage;
            }else{
                if(failCount == 0) {
                    sFlag = "0000";
                    sMessage = "성공";
                    SuccessCheckFlag = true;
                }else if (chk1001 > 0){
                    sFlag = "1001";
                    sMessage = "Push GW Precondition Failed or Not Exist RegistID";
                    check1001Flag = true;
                } else {
                    sFlag = failCode;
                    sMessage = failMessage;
                }
            }

            firstFailCode = StringUtils.defaultIfEmpty(firstFailCode, failCode);

            firstFailMessage = StringUtils.defaultIfEmpty(firstFailMessage, failMessage);

            PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
                    .sType(sType)
                    .sFlag(sFlag)
                    .sMessage(sMessage)
                    .build();

            pushServiceResultDtoArrayList.add(pushServiceResultDto);

        }	// serviceType for end


        //성공일 경우
        if(SuccessCheckFlag) {

            return SendPushResponseDto.builder()
                    .flag("0000")
                    .message("성공")
                    .service(pushServiceResultDtoArrayList)
                    .build();
        }else {

            if(check1001Flag && serviceTypeList.length > 1){

                return SendPushResponseDto.builder()
                        .flag("1001")
                        .message("Push GW Precondition Failed or Not Exist RegistID")
                        .service(pushServiceResultDtoArrayList)
                        .build();
            }else{
                return SendPushResponseDto.builder()
                        .flag(firstFailCode)
                        .message(firstFailMessage)
                        .service(pushServiceResultDtoArrayList)
                        .build();
            }
        }

    }


}
