package com.lguplus.fleta.service.musicshow;

import com.lguplus.fleta.data.dto.request.outer.GetPushRequestDto;
import com.lguplus.fleta.data.dto.request.outer.PostPushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import com.lguplus.fleta.data.entity.PushTargetEntity;
import com.lguplus.fleta.exception.database.DuplicateKeyException;
import com.lguplus.fleta.exception.push.NotFoundException;
import com.lguplus.fleta.repository.musicshow.MusicShowRepository;
import com.lguplus.fleta.util.CommonUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MusicShowDomainService {

    private final MusicShowRepository musicShowRepository;

    public GetPushDto getPush(GetPushRequestDto requestDto) {
        return musicShowRepository.getPush(requestDto);
    }

    public void postPush(PostPushRequestDto requestDto) {
        Integer count = musicShowRepository.validAlbumId(requestDto.getAlbumId());

        if (count == null) {
            throw new NotFoundException();
        }

        GetPushWithPKeyDto getKeyDto = musicShowRepository.getPushWithPkey(requestDto);

        if (getKeyDto == null) {
            //insert
            PushTargetEntity entity = PushTargetEntity.builder()
                .pKey(CommonUtil.generatorPkey(requestDto.getSendDt()))
                .regNo(0) // TODO: 2022/07/19   nextVal
                .saId(requestDto.getSaId())
                .stbMac(requestDto.getStbMac())
                .albumId(requestDto.getAlbumId())
                .categoryId(requestDto.getCategoryId())
                .serviceType(requestDto.getServiceType())
                .msg(requestDto.getMsg())
                .sendDt(requestDto.getSendDt())
                .build();
            musicShowRepository.insertPush(entity);
        } else {
            if (StringUtils.equals(getKeyDto.getPushYn(), requestDto.getPushYn())) {
                throw new DuplicateKeyException();
            } else {
                if (getKeyDto.getPKey() != CommonUtil.generatorPkey(requestDto.getSendDt())) {
                    //deelete
                    PushTargetEntity entity = PushTargetEntity.builder()
                        .pKey(CommonUtil.generatorPkey(requestDto.getSendDt()))
                        .saId(requestDto.getSaId())
                        .stbMac(requestDto.getStbMac())
                        .albumId(requestDto.getAlbumId())
                        .serviceType(requestDto.getServiceType())
                        .build();
                    musicShowRepository.deletePush(entity);
                    //insert
                    PushTargetEntity entity2 = PushTargetEntity.builder()
                        .pKey(CommonUtil.generatorPkey(requestDto.getSendDt()))
                        .regNo(0) // TODO: 2022/07/19   nextVal
                        .saId(requestDto.getSaId())
                        .stbMac(requestDto.getStbMac())
                        .albumId(requestDto.getAlbumId())
                        .categoryId(requestDto.getCategoryId())
                        .serviceType(requestDto.getServiceType())
                        .msg(requestDto.getMsg())
                        .sendDt(requestDto.getSendDt())
                        .build();
                    musicShowRepository.insertPush(entity2);
                } else {
                    //update
                    PushTargetEntity entity = PushTargetEntity.builder()
                        .pushYn("Y")
                        .sendDt(requestDto.getSendDt())
                        .modDt(LocalDateTime.now().toString())
                        .build();
                    musicShowRepository.insertPush(entity);

                }
            }
        }

    }

    public void releasePush(GetPushRequestDto requestDto) {
        Integer count = musicShowRepository.validAlbumId(requestDto.getAlbumId());

        if (count == null) {
            throw new NotFoundException();
        }

        GetPushWithPKeyDto getKeyDto = musicShowRepository.getPushWithPkey(requestDto);

        if (getKeyDto != null) {
            if (StringUtils.equals(getKeyDto.getPushYn(), requestDto.getPushYn())) {
                throw new DuplicateKeyException();
            } else {
                //update
                PushTargetEntity entity = PushTargetEntity.builder()
                    .pushYn("Y")
                    .sendDt(requestDto.getSendDt())
                    .modDt(LocalDateTime.now().toString())
                    .build();
                musicShowRepository.insertPush(entity);
            }
        } else {
            throw new NotFoundException();
        }
    }
}
