package com.lguplus.fleta.repository;

import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.entity.RegistrationIdEntity;

public interface PushRepository {

    //RegistrationId
    RegistrationIdEntity getRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto);
}
