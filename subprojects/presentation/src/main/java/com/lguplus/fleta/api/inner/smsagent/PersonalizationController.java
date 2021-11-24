package com.lguplus.fleta.api.inner.smsagent;

import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.entity.RegistrationIdEntity;
import com.lguplus.fleta.data.vo.SendPushCodeRequestVo;
import com.lguplus.fleta.service.send.PersonalizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REGID response용
 * FeinClient 테스트를 위해 임시로 생성
 * TODO FeinClient 테스트 후 삭제
 *
 * */

@Slf4j
@RequiredArgsConstructor
@RestController
public class PersonalizationController {

    private final PersonalizationService personalizationService;

    @GetMapping("/personalization/registrationId")
    public InnerResponseDto<RegistrationIdEntity> getRegId(SendPushCodeRequestVo sendPushCodeRequestVo) {

        log.debug("PersonalizationController - sendPushCodeRequestVo.getSaId() : {}", sendPushCodeRequestVo.getSaId());
        log.debug("PersonalizationController - sendPushCodeRequestVo.getStbMac() : {}", sendPushCodeRequestVo.getStbMac());

        SendPushCodeRequestDto sendPushCodeRequestDto = sendPushCodeRequestVo.convert();

        return InnerResponseDto.of(personalizationService.loadRegistrationID(sendPushCodeRequestDto));
    }

}
