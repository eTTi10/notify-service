package com.lguplus.fleta.api.outer.uflix;

import com.lguplus.fleta.data.dto.request.outer.UxSimpleJoinSmsRequestDto;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.data.mapper.UxSimpleJoinSmsMapper;
import com.lguplus.fleta.data.vo.UxSimpleJoinSmsRequestVo;
import com.lguplus.fleta.service.uflix.UxSimpleJoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UxSimpleJoinController {

    private final UxSimpleJoinService uxSimpleJoinService;

    private final UxSimpleJoinSmsMapper uxSimpleJoinSmsMapper;

    /**
     * tvG 유플릭스 간편 가입 안내 SMS 요청
     *
     * @param uxSimpleJoinSmsRequestVo tvG 유플릭스 간편 가입 안내 SMS 요청을 위한 VO
     * @return tvG 유플릭스 간편 가입 안내 SMS 요청 결과 응답
     */
    @GetMapping("/smartux/UXSimpleJoin")
    public SmsGatewayResponseDto requestUxSimpleJoinSms(@Valid UxSimpleJoinSmsRequestVo uxSimpleJoinSmsRequestVo) {
        log.debug("==================requestUxSimpleJoinSms BEGIN======================");

        UxSimpleJoinSmsRequestDto uxSimpleJoinSmsRequestDto = uxSimpleJoinSmsMapper.toDto(uxSimpleJoinSmsRequestVo);

        return uxSimpleJoinService.requestUxSimpleJoinSms(uxSimpleJoinSmsRequestDto);
    }

}
