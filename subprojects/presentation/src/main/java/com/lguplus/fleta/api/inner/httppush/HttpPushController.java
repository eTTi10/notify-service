package com.lguplus.fleta.api.inner.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.inner.VariationInfoListRequestDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.entity.UxVariationInfoEntity;
import com.lguplus.fleta.data.vo.HttpPushSingleRequestVo;
import com.lguplus.fleta.data.vo.VariationInfoListVo;
import com.lguplus.fleta.service.VariationInfoService;
import com.lguplus.fleta.service.httppush.HttpPushSingleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pushagent_op")
public class HttpPushController {

    private final HttpPushSingleService httpPushSingleService;


    /**
     * 단건푸시등록
     *
     * @param appId application ID
     * @param serviceId 서비스 ID
     * @param pushType Push발송 타입
     * @param httpPushSingleRequestVo 단건푸시등록을 위한 VO
     * @return 단건푸시등록 결과 응답
     */
    @PostMapping("/v1/push")
    public InnerResponseDto<?> requestHttpPushSingle(
            @RequestParam(value="app_id") @NotBlank(message = "app_id 파라미터값이 전달이 안됨")
            @Size(max = 256, message = "파라미터 app_id는 값이 256 이하이어야 함") String appId,
            @RequestParam(value="service_id") @NotBlank(message = "service_id 파라미터값이 전달이 안됨") String serviceId,
            @RequestParam(value="push_type", required = false) @Pattern(regexp = "^[gaGA]]?$", message = "push_type 파라미터는 값이 G 나 A 이어야 함") String pushType,
            @RequestBody @Valid HttpPushSingleRequestVo httpPushSingleRequestVo
            ) {
        HttpPushSingleRequestDto httpPushSingleRequestDto = httpPushSingleRequestVo.convert();

        return InnerResponseDto.of(httpPushSingleService.requestHttpPushSingle(httpPushSingleRequestDto));
    }
}
