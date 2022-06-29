package com.lguplus.fleta.service.musicshow;

import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.data.dto.GetPushResponseDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicShowService {

    private final MusicShowDomainService musicShowDomainService;

    public GetPushResponseDto getPush(GetPushRequestDto requestDto){
        GetPushDto dto = musicShowDomainService.getPush(requestDto);
        return GetPushResponseDto.create(dto);
    }
}
