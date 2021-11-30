package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.vo.SendPushCodeRequestVo;
import com.lguplus.fleta.service.send.PushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PushController {

    private final PushService pushService;

    @PostMapping("/mims/sendPushCode")
    public SuccessResponseDto sendPushCode(@Valid SendPushCodeRequestVo sendPushCodeRequestVo){

        SendPushCodeRequestDto sendPushCodeRequestDto = sendPushCodeRequestVo.convert();

        return pushService.sendPushCode(sendPushCodeRequestDto);
    }
}
