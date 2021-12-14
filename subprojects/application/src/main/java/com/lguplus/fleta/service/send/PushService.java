package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.RegIdDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.RegistrationIdResponseDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.exception.NotifyHttpPushRuntimeException;
import com.lguplus.fleta.exception.httppush.InvalidSendCodeException;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import com.lguplus.fleta.properties.SendPushCodeProps;
import com.lguplus.fleta.service.httppush.HttpPushDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {


    private final PushDomainService pushDomainService;
    private final HttpPushDomainService httpPushDomainService;
    private final SendPushCodeProps sendPushCodeProps;

    public SuccessResponseDto sendPushCode(SendPushCodeRequestDto sendPushCodeRequestDto) {

        String sendCode = "";
        Map<String, String> pushInfoMap = sendPushCodeProps.findMapBySendCode(sendCode).orElseThrow(InvalidSendCodeException::new);
        String gcmPayLoadBody = pushInfoMap.get("gcm.payload.body");


        //        String registrationId = pushDomainService.getRegistrationID(sendPushCodeRequestDto).getRegId();
//         TODO Personalization Domain의 서비스가 가능할 때 Fein으로 연결하고 아래는 주석처리
        String registrationId = "M00020200205";
        List<String> users = new ArrayList<>();
        users.add(registrationId);



        HttpPushSingleRequestDto httpPushSingleRequestDto = HttpPushSingleRequestDto.builder()
                .appId("")
                .serviceId("")
                .pushType("")
                .msg("")
                .users(users)
                .items(sendPushCodeRequestDto.getItems())
                .build();

        HttpPushResponseDto httpPushResponseDto =  httpPushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);

        if (httpPushResponseDto.getCode().equals("200")) {
            //"성공"
            return SuccessResponseDto.builder().build();
        }
        else {
            throw new NotifyHttpPushRuntimeException();
        }

    }


}
