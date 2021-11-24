package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.entity.RegistrationIdEntity;
import com.lguplus.fleta.repository.PushRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PersonalizationDomainService {

    private final PushRepository pushRepository;
    public RegistrationIdEntity loadRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto) {

        return pushRepository.getRegistrationID(sendPushCodeRequestDto);
    }

}
