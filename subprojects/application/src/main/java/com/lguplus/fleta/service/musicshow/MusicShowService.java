package com.lguplus.fleta.service.musicshow;

import com.lguplus.fleta.data.dto.GetPushResponseDto;
import com.lguplus.fleta.data.dto.PostPushResponseDto;
import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.data.dto.request.outer.PostPushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.exception.musicshow.MusicShowException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicShowService {

    private final MusicShowDomainService musicShowDomainService;

    public GetPushResponseDto getPush(GetPushRequestDto requestDto) {
        GetPushDto dto = musicShowDomainService.getPush(requestDto);
        return GetPushResponseDto.create(dto);
    }

    @Transactional
    public PostPushResponseDto postPush(PostPushRequestDto requestDto) {
        PostPushResponseDto dto = PostPushResponseDto.builder().build();
        try {
            musicShowDomainService.postPush(requestDto);
        } catch (MusicShowException e) {
            dto.setFlag(e.getFlag());
            dto.setMessage(e.getMessage());
        }
        return dto;
    }

    @Transactional
    public PostPushResponseDto releasePush(GetPushRequestDto requestDto) {
        PostPushResponseDto dto = PostPushResponseDto.builder().build();
        try {
            musicShowDomainService.releasePush(requestDto);
        } catch (MusicShowException e) {
            dto.setFlag(e.getFlag());
            dto.setMessage(e.getMessage());
        }
        return dto;
    }
}
