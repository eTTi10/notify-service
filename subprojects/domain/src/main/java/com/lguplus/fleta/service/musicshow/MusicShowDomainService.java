package com.lguplus.fleta.service.musicshow;

import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.repository.musicshow.MusicShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MusicShowDomainService {

    private final MusicShowRepository getPushRepository;

    public GetPushDto getPush(GetPushRequestDto requestDto){
        System.out.println("test" +getPushRepository.getPush(requestDto).getAlbumId());
        return getPushRepository.getPush(requestDto);
    }
}
