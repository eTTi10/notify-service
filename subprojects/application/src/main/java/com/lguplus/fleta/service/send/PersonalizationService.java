package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.entity.RegistrationIdEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PersonalizationService {

    private final PersonalizationDomainService personalizationDomainService;

    public RegistrationIdEntity loadRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto) {

        return personalizationDomainService.loadRegistrationID(sendPushCodeRequestDto);
    }

}
