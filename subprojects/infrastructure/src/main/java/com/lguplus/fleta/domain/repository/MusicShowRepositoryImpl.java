package com.lguplus.fleta.domain.repository;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import com.lguplus.fleta.data.entity.PushTargetEntity;
import com.lguplus.fleta.provider.jpa.MusicShowJpaEmRepository;
import com.lguplus.fleta.provider.jpa.MusicShowJpaRepository;
import com.lguplus.fleta.repository.musicshow.MusicShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MusicShowRepositoryImpl implements MusicShowRepository {

    private final MusicShowJpaRepository jpaRepository;
    private final MusicShowJpaEmRepository emRepository;

    @Override
    public GetPushDto getPush(PushRequestDto requestDto) {
        return emRepository.getPush(requestDto);
    }

    @Override
    public Integer validAlbumId(String albumId) {
        return emRepository.validAlbumId(albumId);
    }

    @Override
    public GetPushWithPKeyDto getPushWithPkey(PushRequestDto requestDto) {
        return emRepository.getPushWithPkey(requestDto);
    }

    @Override
    public PushTargetEntity insertPush(PushTargetEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public void deletePush(PushTargetEntity entity) {
        jpaRepository.deleteBypKeyAndSaIdAndStbMacAndAlbumIdAndServiceType(entity.getPKey(), entity.getSaId(), entity.getStbMac(), entity.getAlbumId(), entity.getServiceType());
    }

    @Override
    public Integer getRegNoNextVal() {
        return emRepository.getRegNoNextVal();
    }
}
