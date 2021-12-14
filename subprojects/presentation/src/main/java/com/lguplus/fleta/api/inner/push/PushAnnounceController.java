package com.lguplus.fleta.api.inner.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.mapper.PushRequestAnnounceMapper;
import com.lguplus.fleta.data.vo.PushRequestBodyAnnounceVo;
import com.lguplus.fleta.data.vo.PushRequestParamVo;
import com.lguplus.fleta.service.push.PushAnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "Push", description = "공지 푸시등록")
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class PushAnnounceController {

    private final PushAnnouncementService pushAnnouncementService;
    private final PushRequestAnnounceMapper pushRequestAnnounceMapper;

    /**
     * 공지 푸시 등록
     *
     * @param pushRequestBodyAnnounceVo Announcement 푸시등록을 위한 VO
     * @return 단건푸시등록 결과 응답
     */
    @ApiOperation(value="공지푸시등록", notes="공지푸시를 등록한다.")
    @PostMapping(value = "/notify/push/announcement")
    public InnerResponseDto<PushClientResponseDto> pushRequestAnnouncement(
            @RequestBody @Valid PushRequestBodyAnnounceVo pushRequestBodyAnnounceVo) {

        log.debug("PushAnnounceController : {}", pushRequestBodyAnnounceVo);

        PushRequestAnnounceDto dto = pushRequestAnnounceMapper.toDto(pushRequestBodyAnnounceVo);

        return InnerResponseDto.of(pushAnnouncementService.requestAnnouncement(dto));
    }

}
