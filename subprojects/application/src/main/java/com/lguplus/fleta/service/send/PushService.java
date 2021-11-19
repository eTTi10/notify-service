package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.RegistrationIdResponseDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.entity.RegistrationIdEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushService {

    private final PushDomainService pushDomainService;

    public RegistrationIdResponseDto sendPushCode(SendPushCodeRequestDto sendPushCodeRequestDto) {


        //TODO http push domain service에 연결 21.11.17 moutlaw
//        String status = httpPushSingleDomainService.requestHttpPushSingle(httpPushSingleRequestDto);
        String registrationId = "";
        registrationId = getRegistrationID(sendPushCodeRequestDto);

        return RegistrationIdResponseDto.builder()
                .regId(registrationId)
                .build();
    }


    public RegistrationIdResponseDto getRegId(SendPushCodeRequestDto sendPushCodeRequestDto) {

        String registrationId = "";
        registrationId = loadRegistrationID(sendPushCodeRequestDto).getRegId();

        log.debug("registrationId : {}", registrationId );

        return RegistrationIdResponseDto.builder()
                .regId(registrationId)
                .build();
    }

    public String getRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto) {

        Map<String, RegistrationIdResponseDto> resultMap = pushDomainService.getRegistrationID(sendPushCodeRequestDto);
        String regId = resultMap.toString();
        return regId;
    }

    public RegistrationIdEntity loadRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto) {

        return pushDomainService.loadRegistrationID(sendPushCodeRequestDto);
    }
}
