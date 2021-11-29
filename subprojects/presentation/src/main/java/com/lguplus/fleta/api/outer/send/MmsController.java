package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.data.dto.request.SendMmsRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.SendMmsVo;
import com.lguplus.fleta.service.send.MmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MmsController {

    private final MmsService smsService;

    /**
     * MMS전송요청
     * mms_cd
     * M001 : 모바일tv 앱 설치안내 문자
     * M002 : 프로야구 앱 설치안내 문자
     * M003 : 아이들나라 앱 설치안내 문자
     * M004 : 골프 앱 설치안내 문자
     * M005 : 아이돌Live 앱 설치안내 문자
     *
     * sa_id:가입자정보
     * stb_mac:가입자 STB MAC Address
     * mms_cd:MMS 메시지 코드
     * ctn:발송대상 번호
     * replacement:치환문자
     *
     * replacement
     * 치환하고자 하는 값 입력
     * 예) 김철수|냉장고
     * #모바일로 페어링 요청 알림 MMS발송일 경우 트랜젝션 ID 입력 됨
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/mims/sendMms")
    public SuccessResponseDto setPayment(@Valid SendMmsVo request) throws Exception {

        log.debug("SmsController.setPayment() - {}:{}", "MMS발송 요청", request);

        SendMmsRequestDto requestDto = request.convert();

        return smsService.sendMms(requestDto);
    }

}
