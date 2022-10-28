package com.lguplus.fleta.service.musicshow;

import com.lguplus.fleta.client.VodlookupClient;
import com.lguplus.fleta.data.constant.MusicShowConstants;
import com.lguplus.fleta.data.dto.AlbumProgrammingDto;
import com.lguplus.fleta.data.dto.request.outer.PushRequestDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import com.lguplus.fleta.data.dto.response.outer.GetPushWithPKeyDto;
import com.lguplus.fleta.data.entity.PushTarget;
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

    public PushTarget postPush(PushRequestDto requestDto) {

        List<AlbumProgrammingDto> validAlbumList = vodlookupClient.getAlbumProgramming(MusicShowConstants.CATEGORY_TYPE_MOBILE, List.of(requestDto.getAlbumId()));

        if (validAlbumList == null || validAlbumList.isEmpty()) {
            throw new NoResultException();
        }

        GetPushWithPKeyDto getKeyDto = musicShowRepository.getPushWithPkey(requestDto);

        if (getKeyDto == null) {
            return insertPush(requestDto);
        }

        if (StringUtils.equals(getKeyDto.getPushYn(), requestDto.getPushYn())) {
            throw new SubscriberAlreadyExistsException();
        }

        if (!getKeyDto.getPKey().equals(CommonUtil.generatorPkey(requestDto.getSendDt()))) {
            deletePush(getKeyDto);
            return insertPush(requestDto);
        } else {
            return updatePush(requestDto, getKeyDto, MusicShowConstants.PUSH_YN_Y);
        }

    }

    public PushTarget releasePush(PushRequestDto requestDto) {

        List<AlbumProgrammingDto> validAlbumList = vodlookupClient.getAlbumProgramming(MusicShowConstants.CATEGORY_TYPE_MOBILE, List.of(requestDto.getAlbumId()));

        if (validAlbumList == null || validAlbumList.isEmpty()) {
            throw new NoResultException();
        }

        GetPushWithPKeyDto getKeyDto = musicShowRepository.getPushWithPkey(requestDto);

        if (getKeyDto == null) {
            throw new NoResultException();
        }

        if (StringUtils.equals(getKeyDto.getPushYn(), requestDto.getPushYn())) {
            throw new SubscriberAlreadyExistsException();
        } else {
            return updatePush(requestDto, getKeyDto, MusicShowConstants.PUSH_YN_N);
        }

    }

    public Timestamp convertFormat(String sendDt) {

        if (StringUtils.isBlank(sendDt))
            return null;

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

    public PushTarget insertPush(PushRequestDto requestDto) {
        PushTarget entity = PushTarget.builder()
            .pKey(CommonUtil.generatorPkey(requestDto.getSendDt()))
            .regNo(musicShowRepository.getRegNoNextVal())
            .saId(requestDto.getSaId())
            .stbMac(requestDto.getStbMac())
            .albumId(requestDto.getAlbumId())
            .categoryId(requestDto.getCategoryId())
            .serviceType(requestDto.getServiceType())
            .msg(requestDto.getMsg())
            .pushYn(MusicShowConstants.PUSH_YN_Y) //기존 oracle에서 default값
            .resultCode(MusicShowConstants.RESULT_CODE_DEFAULT) //기존 oracle에서 default값
            .regDt(Timestamp.valueOf(LocalDateTime.now())) //기존 oracle에서 default값
            .sendDt(convertFormat(requestDto.getSendDt()))
            .build();
        return musicShowRepository.insertPush(entity);
    }

    public PushTarget updatePush(PushRequestDto requestDto, GetPushWithPKeyDto getKeyDto, String pushYn) {
        String sendDt = requestDto.getSendDt();

        if (pushYn.equals(MusicShowConstants.PUSH_YN_N))
            sendDt = getKeyDto.getStartDt();

        PushTarget entity = PushTarget.builder()
            .pKey(getKeyDto.getPKey())
            .regNo(getKeyDto.getRegNo())
            .saId(getKeyDto.getSaId())
            .stbMac(getKeyDto.getStbMac())
            .albumId(getKeyDto.getAlbumId())
            .categoryId(getKeyDto.getCategoryId())
            .serviceType(getKeyDto.getServiceType())
            .msg(getKeyDto.getMsg())
            .pushYn(pushYn)
            .resultCode(getKeyDto.getResultCode())
            .regDt(StringUtils.isNotBlank(getKeyDto.getRegDt()) ? Timestamp.valueOf(getKeyDto.getRegDt()) : null)
            .sendDt(convertFormat(sendDt))
            .modDt(Timestamp.valueOf(LocalDateTime.now()))
            .build();

        return musicShowRepository.insertPush(entity);
    }

    public void deletePush(GetPushWithPKeyDto getKeyDto) {
        PushTarget entity = PushTarget.builder()
            .pKey(getKeyDto.getPKey())
            .saId(getKeyDto.getSaId())
            .stbMac(getKeyDto.getStbMac())
            .albumId(getKeyDto.getAlbumId())
            .serviceType(getKeyDto.getServiceType())
            .build();
        musicShowRepository.deletePush(entity);
    }
}
