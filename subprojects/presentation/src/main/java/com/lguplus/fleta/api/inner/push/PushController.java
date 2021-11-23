package com.lguplus.fleta.api.inner.push;

import com.lguplus.fleta.data.dto.request.inner.PushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.vo.PushRequestVo;
import com.lguplus.fleta.data.vo.PushSingleRequestVo;
import com.lguplus.fleta.service.push.PushSingleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class PushController {

    private final PushSingleService pushSingleService;

    //@Value("${spring.profiles.active}")
    //private String env;


    /**
     * 단건푸시등록
     *
     * @param pushRequestVo application ID, 서비스 ID, Push발송 타입
     * @param pushSingleRequestVo 단건푸시등록을 위한 VO
     * @return 단건푸시등록 결과 응답
     */
    @PostMapping(value = "/smartux/v1/push", consumes = MediaType.APPLICATION_XML_VALUE)
    public InnerResponseDto<?> pushRequest(@Valid PushRequestVo pushRequestVo,
            @RequestBody @Valid PushSingleRequestVo pushSingleRequestVo
    ) {

        if(StringUtils.isBlank(pushRequestVo.getPushType())) {
            pushRequestVo.setPushType("G"); //Default
        }

        PushSingleRequestDto pushSingleRequestDto = pushSingleRequestVo.convert(pushRequestVo);

        return InnerResponseDto.of(pushSingleService.requestPushSingle(pushSingleRequestDto));
    }

}
