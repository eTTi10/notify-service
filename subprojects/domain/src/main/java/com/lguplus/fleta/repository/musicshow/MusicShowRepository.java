package com.lguplus.fleta.repository.musicshow;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import com.lguplus.fleta.data.entity.PushTarget;

public interface MusicShowRepository {

    GetPushDto getPush(PushRequestDto requestDto);

    GetPushWithPKeyDto getPushWithPkey(PushRequestDto requestDto);

    PushTarget insertPush(PushTarget entity);

    void deletePush(PushTarget entity);

    Integer getRegNoNextVal();
}
