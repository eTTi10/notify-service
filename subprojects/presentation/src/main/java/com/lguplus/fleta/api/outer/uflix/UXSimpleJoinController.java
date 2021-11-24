package com.lguplus.fleta.api.outer.uflix;

import com.lguplus.fleta.data.dto.request.outer.UXSimpleJoinSmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.UXSimpleJoinSmsRequestVo;
import com.lguplus.fleta.service.uflix.UXSimpleJoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UXSimpleJoinController {

    private final UXSimpleJoinService uxSimpleJoinService;

    /**
     * tvG 유플릭스 간편 가입 안내 SMS 요청
     *
     * @param uxSimpleJoinSmsRequestVo tvG 유플릭스 간편 가입 안내 SMS 요청을 위한 VO
     * @return tvG 유플릭스 간편 가입 안내 SMS 요청 결과 응답
     */
    @GetMapping("/smartux/UXSimpleJoin")
    public SuccessResponseDto requestUXSimpleJoinSms(@Valid UXSimpleJoinSmsRequestVo uxSimpleJoinSmsRequestVo) {
        UXSimpleJoinSmsRequestDto uxSimpleJoinSmsRequestDto = uxSimpleJoinSmsRequestVo.convert();

        return uxSimpleJoinService.requestUXSimpleJoinSms(uxSimpleJoinSmsRequestDto);
    }
}
