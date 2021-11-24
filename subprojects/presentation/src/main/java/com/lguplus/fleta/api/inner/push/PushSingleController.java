package com.lguplus.fleta.api.inner.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.vo.PushRequestParamVo;
import com.lguplus.fleta.data.vo.PushRequestBodySingleVo;
import com.lguplus.fleta.service.push.PushSingleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class PushSingleController {

    private final PushSingleService pushSingleService;

    //@Value("${spring.profiles.active}")
    //private String env;


    /**
     * 단건푸시등록
     *
     * @param pushRequestParamVo application ID, 서비스 ID, Push발송 타입
     * @param pushRequestBodySingleVo 단건푸시등록을 위한 VO
     * @return 단건푸시등록 결과 응답
     */
    @PostMapping(value = "/smartux/v1/push", consumes = MediaType.APPLICATION_XML_VALUE)
    public InnerResponseDto<?> pushRequest(
            @Valid PushRequestParamVo pushRequestParamVo,
            @RequestBody @Valid PushRequestBodySingleVo pushRequestBodySingleVo) {
        PushRequestSingleDto pushRequestSingleDto = pushRequestBodySingleVo.convert(pushRequestParamVo);
        return InnerResponseDto.of(pushSingleService.requestPushSingle(pushRequestSingleDto));
    }

}
