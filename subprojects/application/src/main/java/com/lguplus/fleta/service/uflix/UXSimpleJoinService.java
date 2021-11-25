package com.lguplus.fleta.service.uflix;

import com.lguplus.fleta.data.dto.request.outer.UXSimpleJoinSmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UXSimpleJoinService {

    /**
     * tvG 유플릭스 간편 가입 안내 SMS 요청
     *
     * @param uxSimpleJoinSmsRequestDto tvG 유플릭스 간편 가입 안내 SMS 요청을 위한 DTO
     * @return tvG 유플릭스 간편 가입 안내 SMS 요청 결과 응답
     */
    public SuccessResponseDto requestUXSimpleJoinSms(UXSimpleJoinSmsRequestDto uxSimpleJoinSmsRequestDto) {
        log.debug("==================requestUXSimpleJoinSms======================");

        // TODO : SMS 전송 함수 호출

        return SuccessResponseDto.builder().build();
    }
}
