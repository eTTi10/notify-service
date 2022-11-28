package com.lguplus.fleta.service.musicshow;

import com.lguplus.fleta.data.dto.GetPushResponseDto;
import com.lguplus.fleta.data.dto.PostPushResponseDto;
import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicShowService {

    private final MusicShowDomainService musicShowDomainService;

    public GetPushResponseDto getPush(PushRequestDto requestDto) {
        GetPushDto dto = musicShowDomainService.getPush(requestDto);
        return GetPushResponseDto.create(dto);
    }

    @Transactional
    public PostPushResponseDto postPush(PushRequestDto requestDto) {
        PostPushResponseDto dto = PostPushResponseDto.builder().build();
        musicShowDomainService.postPush(requestDto);
        dto.setSuccessFlag();
        return dto;
    }

    @Transactional
    public PostPushResponseDto releasePush(PushRequestDto requestDto) {
        PostPushResponseDto dto = PostPushResponseDto.builder().build();
        musicShowDomainService.releasePush(requestDto);
        dto.setSuccessFlag();
        return dto;
    }
}
