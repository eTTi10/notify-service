package com.lguplus.fleta.api.inner.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.vo.PushRequestBodyMultiVo;
import com.lguplus.fleta.data.vo.PushRequestParamMultiVo;
import com.lguplus.fleta.service.push.PushMultiService;
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
public class PushMultiController {

    private final PushMultiService pushMultiService;

    //@Value("${spring.profiles.active}")
    //private String env;


    /**
     * 단건푸시등록
     *
     * @param pushRequestParamMultiVo application ID, 서비스 ID, Push발송 타입
     * @param pushRequestParamMultiVo Announcement 푸시등록을 위한 VO
     * @return 단건푸시등록 결과 응답
     */
    @PostMapping(value = "/notify/push/multi", consumes = MediaType.APPLICATION_XML_VALUE)
    public InnerResponseDto<?> multiPushRequest(
            @Valid PushRequestParamMultiVo pushRequestParamMultiVo,
            @RequestBody @Valid PushRequestBodyMultiVo pushRequestBodyMultiVo) {

        PushRequestMultiDto dto = pushRequestBodyMultiVo.convert(pushRequestParamMultiVo);

        return InnerResponseDto.of(pushMultiService.requestMultiPush(dto));
    }

}
