package com.lguplus.fleta.api.inner.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushAnnounceRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.mapper.HttpPushAnnounceMapper;
import com.lguplus.fleta.data.vo.HttpPushAnnounceRequestVo;
import com.lguplus.fleta.service.httppush.HttpAnnouncementPushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Http AnnouncementPush RestController
 * <p>
 * 공지 푸시등록
 */
@Api(tags = "HttpPush 공지 푸시등록")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notify")
public class HttpAnnouncementPushController {

    private final HttpAnnouncementPushService httpAnnouncementPushService;

    private final HttpPushAnnounceMapper httpPushAnnounceMapper;


    /**
     * 공지푸시등록
     *
     * @param httpPushAnnounceRequestVo 공지푸시등록을 위한 VO
     * @return 공지푸시등록 결과 응답
     */
    @ApiOperation(value = "공지푸시등록", notes = "공지푸시를 등록한다.")
    @PostMapping(value = "/httppush/announcement")
    public InnerResponseDto<HttpPushResponseDto> requestHttpPushAnnouncement(@RequestBody @Valid HttpPushAnnounceRequestVo httpPushAnnounceRequestVo) {
        log.debug("==================공지푸시등록 BEGIN======================");
        HttpPushAnnounceRequestDto httpPushAnnounceRequestDto = httpPushAnnounceMapper.toDto(httpPushAnnounceRequestVo);

        log.debug("mapstruct httpPushAnnounceRequestDto :::::::::::::::::::::::::: {}", httpPushAnnounceRequestDto);
        log.debug("==================공지푸시등록 END======================");

        // 성공
        return InnerResponseDto.of(httpAnnouncementPushService.requestHttpPushAnnouncement(httpPushAnnounceRequestDto));
    }

}
