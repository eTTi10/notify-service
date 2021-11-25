package com.lguplus.fleta.api.inner.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.vo.PushRequestBodyAnnounceVo;
import com.lguplus.fleta.data.vo.PushRequestParamVo;
import com.lguplus.fleta.service.push.PushAnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class PushAnnounceController {

    private final PushAnnouncementService pushAnnouncementService;

    //@Value("${spring.profiles.active}")
    //private String env;


    /**
     * 공지 푸시 등록
     *
     * @param pushRequestBodyAnnounceVo Announcement 푸시등록을 위한 VO
     * @return 단건푸시등록 결과 응답
     */
    @PostMapping(value = "/smartux/v1/announcement")
    public InnerResponseDto<PushClientResponseDto> pushRequestAnnouncement(
            @RequestBody @Valid PushRequestBodyAnnounceVo pushRequestBodyAnnounceVo) {

        log.debug("PushAnnounceController : {}", pushRequestBodyAnnounceVo);

        PushRequestAnnounceDto dto = pushRequestBodyAnnounceVo.convert();

        return InnerResponseDto.of(pushAnnouncementService.requestAnnouncement(dto));
    }

}
