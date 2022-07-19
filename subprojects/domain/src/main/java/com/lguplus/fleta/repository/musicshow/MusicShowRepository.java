package com.lguplus.fleta.repository.musicshow;

import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import com.lguplus.fleta.data.entity.PushTargetEntity;

public interface MusicShowRepository {

    GetPushDto getPush(GetPushRequestDto requestDto);

    Integer validAlbumId(String albumId);

    GetPushWithPKeyDto getPushWithPkey(PushRequestDto requestDto);

    PushTargetEntity insertPush(PushTargetEntity entity);

    void deletePush(PushTargetEntity entity);
}
