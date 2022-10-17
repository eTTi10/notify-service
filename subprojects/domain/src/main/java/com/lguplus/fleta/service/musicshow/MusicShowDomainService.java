package com.lguplus.fleta.service.musicshow;

import com.lguplus.fleta.client.VodlookupClient;
import com.lguplus.fleta.data.dto.AlbumProgrammingDto;
import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import com.lguplus.fleta.data.entity.PushTargetEntity;
import com.lguplus.fleta.exception.NoResultException;
import com.lguplus.fleta.exception.subscription.SubscriberAlreadyExistsException;
import com.lguplus.fleta.repository.musicshow.MusicShowRepository;
import com.lguplus.fleta.util.CommonUtil;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MusicShowDomainService {

    private final MusicShowRepository musicShowRepository;
    private final VodlookupClient vodlookupClient;

    public GetPushDto getPush(PushRequestDto requestDto) {
        return musicShowRepository.getPush(requestDto);
    }

    public PushTargetEntity postPush(PushRequestDto requestDto) {

        PushTargetEntity resultEntity;

        List<AlbumProgrammingDto> validAlbumList = vodlookupClient.getAlbumProgramming("NSC", List.of(requestDto.getAlbumId()));

        Integer count = validAlbumList.size();

        if (count == null || count.intValue() < 1) {
            throw new NoResultException();
        }

        GetPushWithPKeyDto getKeyDto = musicShowRepository.getPushWithPkey(requestDto);
        Integer regNo = musicShowRepository.getRegNoNextVal();

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
                .pushYn("Y") //기존 oracle에서 default값
                .resultCode("00") //기존 oracle에서 default값
                .regDt(Timestamp.valueOf(LocalDateTime.now())) //기존 oracle에서 default값
                .sendDt(convertFormat(requestDto.getSendDt()))
                .build();
            resultEntity = musicShowRepository.insertPush(entity);
        } else {
            if (StringUtils.equals(getKeyDto.getPushYn(), requestDto.getPushYn())) {
                throw new SubscriberAlreadyExistsException();
            } else {
                if (getKeyDto.getPKey() != CommonUtil.generatorPkey(requestDto.getSendDt())) {
                    //deelete
                    PushTargetEntity entity = PushTargetEntity.builder()
                        .pKey(getKeyDto.getPKey())
                        .saId(getKeyDto.getSaId())
                        .stbMac(getKeyDto.getStbMac())
                        .albumId(getKeyDto.getAlbumId())
                        .serviceType(getKeyDto.getServiceType())
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
                        .pushYn("Y") //기존 oracle에서 default값
                        .resultCode("00") //기존 oracle에서 default값
                        .regDt(Timestamp.valueOf(LocalDateTime.now())) //기존 oracle에서 default값
                        .sendDt(convertFormat(requestDto.getSendDt()))
                        .build();
                    resultEntity = musicShowRepository.insertPush(entity2);
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
                        .pushYn("Y") //const 변경
                        .resultCode(getKeyDto.getResultCode())
                        .regDt(getKeyDto.getRegDt() != null ? Timestamp.valueOf(getKeyDto.getRegDt()) : null)
                        .sendDt(requestDto.getSendDt() != null ? convertFormat(requestDto.getSendDt()) : null)
                        .modDt(Timestamp.valueOf(LocalDateTime.now()))
                        .build();
                    resultEntity = musicShowRepository.insertPush(entity);
                }
            }
        }
        return resultEntity;
    }

    public PushTargetEntity releasePush(PushRequestDto requestDto) {

        PushTargetEntity resultEntity;

        List<AlbumProgrammingDto> validAlbumList = vodlookupClient.getAlbumProgramming("NSC", List.of(requestDto.getAlbumId()));

        Integer count = validAlbumList.size();

        if (count == null || count.intValue() < 1) {
            throw new NoResultException();
        }

        GetPushWithPKeyDto getKeyDto = musicShowRepository.getPushWithPkey(requestDto);

        if (getKeyDto != null) {
            if (StringUtils.equals(getKeyDto.getPushYn(), requestDto.getPushYn())) {
                throw new SubscriberAlreadyExistsException();
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
                    .sendDt(convertFormat(getKeyDto.getStartDt()))
                    .modDt(Timestamp.valueOf(LocalDateTime.now()))
                    .build();

                resultEntity = musicShowRepository.insertPush(entity);
            }
        } else {
            throw new NoResultException();
        }

        return resultEntity;
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
