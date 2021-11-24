package com.lguplus.fleta.api.inner.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
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
     * 단건푸시등록
     *
     * @param pushRequestParamVo application ID, 서비스 ID, Push발송 타입
     * @param pushRequestBodyAnnounceVo Announcement 푸시등록을 위한 VO
     * @return 단건푸시등록 결과 응답
     */
    @PostMapping(value = "/smartux/v1/announcement", consumes = MediaType.APPLICATION_XML_VALUE)
    public InnerResponseDto<?> pushRequestAnnouncement(
            @Valid PushRequestParamVo pushRequestParamVo,
            @RequestBody @Valid PushRequestBodyAnnounceVo pushRequestBodyAnnounceVo) {

        PushRequestAnnounceDto dto = pushRequestBodyAnnounceVo.convert(pushRequestParamVo);

        return InnerResponseDto.of(pushAnnouncementService.requestAnnouncement(dto));
    }

}
