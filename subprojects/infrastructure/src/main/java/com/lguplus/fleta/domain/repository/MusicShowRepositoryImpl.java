package com.lguplus.fleta.domain.repository;

import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.provider.jpa.MusicShowJpaEmRepository;
import com.lguplus.fleta.repository.musicshow.MusicShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MusicShowRepositoryImpl implements MusicShowRepository {

    private final MusicShowJpaEmRepository emRepository;

    @Override
    public GetPushDto getPush(GetPushRequestDto requestDto) {
        return emRepository.getPush1(requestDto);
    }
}
