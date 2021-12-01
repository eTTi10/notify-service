package com.lguplus.fleta.api.inner.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushAnnounceRequestDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.data.vo.HttpPushAnnounceRequestVo;
import com.lguplus.fleta.service.httppush.HttpPushAnnounceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Http Push Announce RestController
 *
 * 공지 푸시등록
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notify")
public class HttpPushAnnounceController {

    private final HttpPushAnnounceService httpPushAnnounceService;


    /**
     * 공지푸시등록
     *
     * @param httpPushAnnounceRequestVo 공지푸시등록을 위한 VO
     * @return 공지푸시등록 결과 응답
     */
    @PostMapping(value = "/httppush/announcement")
    public InnerResponseDto<HttpPushResponseDto> requestHttpPushAnnounce(@RequestBody @Valid HttpPushAnnounceRequestVo httpPushAnnounceRequestVo) {
        log.debug("==================공지푸시등록 BEGIN======================");
        HttpPushAnnounceRequestDto httpPushAnnounceRequestDto = httpPushAnnounceRequestVo.convert();

        HttpPushResponseDto httpPushResponseDto = httpPushAnnounceService.requestHttpPushAnnounce(httpPushAnnounceRequestDto);

        log.debug("httpPushResponseDto :::::::::::::::::::: {}", httpPushResponseDto);
        log.debug("==================공지푸시등록 END======================");

        // 성공
        if (httpPushResponseDto.getCode().equals("200")) {
            return InnerResponseDto.of(httpPushResponseDto);
        }

        // 실패
        return InnerResponseDto.of(InnerResponseCodeType.HTTP_PUSH_SERVER_ERROR, httpPushResponseDto);
    }

}
