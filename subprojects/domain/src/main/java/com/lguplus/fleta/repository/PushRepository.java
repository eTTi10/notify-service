package com.lguplus.fleta.repository;

import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.entity.RegistrationIdEntity;


/**
 * REGID 조회 용
 * TODO Feign으로 변경되어 삭제예정
 *
 * */
public interface PushRepository {

    //RegistrationId
    RegistrationIdEntity getRegistrationID(SendPushCodeRequestDto sendPushCodeRequestDto);
}
