package com.lguplus.fleta.repository.musicshow;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import com.lguplus.fleta.data.entity.PushTargetEntity;

public interface MusicShowRepository {

    GetPushDto getPush(PushRequestDto requestDto);

    GetPushWithPKeyDto getPushWithPkey(PushRequestDto requestDto);

    PushTargetEntity insertPush(PushTargetEntity entity);

    void deletePush(PushTargetEntity entity);

    Integer getRegNoNextVal();
}
