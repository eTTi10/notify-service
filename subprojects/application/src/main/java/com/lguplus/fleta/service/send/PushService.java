package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.RegistrationIdResponseDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.entity.RegistrationIdEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {

    private final PushDomainService pushDomainService;

    public RegistrationIdResponseDto sendPushCode(SendPushCodeRequestDto sendPushCodeRequestDto) {


        //TODO http push domain service에 연결 21.11.17 moutlaw
//        String status = httpPushSingleDomainService.requestHttpPushSingle(httpPushSingleRequestDto);
        String registrationId = "";
        registrationId = getRegistrationID(sendPushCodeRequestDto).getRegId();

        return RegistrationIdResponseDto.builder()
                .regId(registrationId)
                .build();
    }

    public RegistrationIdEntity getRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto) {

        return pushDomainService.getRegistrationID(sendPushCodeRequestDto);
    }

}
