package com.lguplus.fleta.repository.musicshow;

import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;

public interface MusicShowRepository {
    GetPushDto getPush(GetPushRequestDto requestDto);
}
