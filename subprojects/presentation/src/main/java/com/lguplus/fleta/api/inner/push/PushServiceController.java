package com.lguplus.fleta.api.inner.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.*;
import com.lguplus.fleta.data.mapper.PushRequestMapper;
import com.lguplus.fleta.data.vo.PushRequestBodyAnnounceVo;
import com.lguplus.fleta.data.vo.PushRequestBodyMultiVo;
import com.lguplus.fleta.data.vo.PushRequestBodySingleVo;
import com.lguplus.fleta.service.push.PushAnnouncementService;
import com.lguplus.fleta.service.push.PushMultiService;
import com.lguplus.fleta.service.push.PushSingleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Api(tags = "Push", description = "Push Message 전송 서비스")
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class PushServiceController {

    private final PushAnnouncementService pushAnnouncementService;

    private final PushSingleService pushSingleService;

    private final PushMultiService pushMultiService;

    private final PushRequestMapper pushRequestMapper;

    @Value("${push-comm.push.reject.regList}")
    private String pushRejectRegList;


    /**
     * 공지 푸시 등록
     *
     * @param pushRequestBodyAnnounceVo Announcement 푸시등록을 위한 VO
     * @return 공지 푸시 등록 결과 응답
     */
    @ApiOperation(value="공지 Push Message 등록", notes="공지 Push Message를 등록한다.")
    @PostMapping(value = "/notify/push/announcement")
    public InnerResponseDto<PushClientResponseDto> pushRequestAnnouncement(
            @RequestBody @Valid PushRequestBodyAnnounceVo pushRequestBodyAnnounceVo) {

        log.debug("PushAnnounceController : {}", pushRequestBodyAnnounceVo);

        PushRequestAnnounceDto pushRequestAnnounceDto = pushRequestMapper.toDtoAnnounce(pushRequestBodyAnnounceVo);

        return InnerResponseDto.of(pushAnnouncementService.requestAnnouncement(pushRequestAnnounceDto));
    }

    /**
     * 단건푸시등록
     *
     * @param pushRequestBodySingleVo 단건푸시등록을 위한 VO
     * @return 단건푸시등록 결과 응답
     */
    @ApiOperation(value="단건 Push Message 등록", notes="단건 Push Message를 등록한다.")
    @PostMapping(value = "/notify/push/single")
    public InnerResponseDto<PushClientResponseDto> pushRequest(
            @RequestBody @Valid PushRequestBodySingleVo pushRequestBodySingleVo) {

        //log.debug("PushSingleController : {}", pushRequestBodySingleVo)
        PushRequestSingleDto pushRequestSingleDto = pushRequestMapper.toDtoSingle(pushRequestBodySingleVo);

        //Reject User
        if (!isValidRegId(pushRequestSingleDto.getRegId())) {
            return InnerResponseDto.of(PushClientResponseDto.builder().code("0000").message("성공").build());
        }

        return InnerResponseDto.of(pushSingleService.requestPushSingle(pushRequestSingleDto));
    }

    /**
     * Multi 푸시등록
     *
     * @param pushRequestBodyMultiVo 푸시등록을 위한 VO
     * @return Multi 푸시등록 결과 응답
     */
    @PostMapping(value = "/notify/push/multi")
    @ApiOperation(value="Multi Push Message 등록", notes="Multi Push Message를 등록한다.")
    public InnerResponseDto<PushClientResponseMultiDto> multiPushRequest(
            @RequestBody @Valid PushRequestBodyMultiVo pushRequestBodyMultiVo) {

        //Reject User Filtering
        pushRequestBodyMultiVo.setUsers(pushRequestBodyMultiVo.getUsers().stream().filter(this::isValidRegId).collect(Collectors.toList()));

        PushRequestMultiDto dto = pushRequestMapper.toDtoMulti(pushRequestBodyMultiVo);

        PushClientResponseMultiDto responseMultiDto = pushMultiService.requestMultiPush(dto);

        return InnerResponseDto.of(responseMultiDto);
    }

    private boolean isValidRegId(String regId) {
        return !("|" + this.pushRejectRegList + "|").contains("|" + regId + "|");
    }
}
