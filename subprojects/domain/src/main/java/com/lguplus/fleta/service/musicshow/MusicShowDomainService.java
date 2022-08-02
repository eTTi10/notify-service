package com.lguplus.fleta.service.musicshow;

import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import com.lguplus.fleta.data.entity.PushTargetEntity;
import com.lguplus.fleta.exception.database.DuplicateKeyException;
import com.lguplus.fleta.exception.push.NotFoundException;
import com.lguplus.fleta.repository.musicshow.MusicShowRepository;
import com.lguplus.fleta.util.CommonUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MusicShowDomainService {

    private final MusicShowRepository musicShowRepository;

    public GetPushDto getPush(PushRequestDto requestDto) {
        return musicShowRepository.getPush(requestDto);
    }

    public void postPush(PushRequestDto requestDto) {
        Integer count = musicShowRepository.validAlbumId(requestDto.getAlbumId());

        if (count == null || count.intValue() < 1) {
            throw new NotFoundException();
        }

        GetPushWithPKeyDto getKeyDto = musicShowRepository.getPushWithPkey(requestDto);
        Integer regNo = 0;
        //        regNo = musicShowRepository.getRegNoNextVal();

        if (getKeyDto == null) {
            //insert
            PushTargetEntity entity = PushTargetEntity.builder()
                .pKey(CommonUtil.generatorPkey(requestDto.getSendDt()))
                .regNo(regNo)
                .saId(requestDto.getSaId())
                .stbMac(requestDto.getStbMac())
                .albumId(requestDto.getAlbumId())
                .categoryId(requestDto.getCategoryId())
                .serviceType(requestDto.getServiceType())
                .msg(requestDto.getMsg())
                .sendDt(convertFormat(requestDto.getSendDt()))
                .modDt(null)
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
                        .regNo(regNo)
                        .saId(requestDto.getSaId())
                        .stbMac(requestDto.getStbMac())
                        .albumId(requestDto.getAlbumId())
                        .categoryId(requestDto.getCategoryId())
                        .serviceType(requestDto.getServiceType())
                        .msg(requestDto.getMsg())
                        .sendDt(convertFormat(requestDto.getSendDt()))
                        .build();
                    musicShowRepository.insertPush(entity2);
                } else {
                    //update
                    PushTargetEntity entity = PushTargetEntity.builder()
                        .regNo(regNo)
                        .pushYn("Y")
                        .sendDt(convertFormat(requestDto.getSendDt()))
                        .modDt(Timestamp.valueOf(LocalDateTime.now()))
                        .build();
                    musicShowRepository.insertPush(entity);

                }
            }
        }

    }

    public void releasePush(PushRequestDto requestDto) {
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
                    .pKey(getKeyDto.getPKey())
                    .regNo(getKeyDto.getRegNo())
                    .saId(getKeyDto.getSaId())
                    .stbMac(getKeyDto.getStbMac())
                    .albumId(getKeyDto.getAlbumId())
                    .categoryId(getKeyDto.getCategoryId())
                    .serviceType(getKeyDto.getServiceType())
                    .msg(getKeyDto.getMsg())
                    .pushYn("N")
                    .resultCode(getKeyDto.getResultCode())
                    .regDt(getKeyDto.getRegDt() != null ? Timestamp.valueOf(getKeyDto.getRegDt()) : null)
                    .sendDt(getKeyDto.getStartDt() != null ? convertFormat(getKeyDto.getStartDt()) : null)
                    .modDt(Timestamp.valueOf(LocalDateTime.now()))
                    .build();

                musicShowRepository.insertPush(entity);
            }
        } else {
            throw new NotFoundException();
        }
    }

    public Timestamp convertFormat(String sendDt) {
        SimpleDateFormat fromFormat = new SimpleDateFormat("yyyyMMddhhmm");
        SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.s");

        Date fromDate;
        try {
            fromDate = fromFormat.parse(sendDt);
        } catch (ParseException e) {
            throw new NumberFormatException();
        }
        String toDate = toFormat.format(fromDate);

        return Timestamp.valueOf(toDate);
    }

}
