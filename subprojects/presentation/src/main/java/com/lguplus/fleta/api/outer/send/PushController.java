package com.lguplus.fleta.api.outer.send;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PushController {

//    private final PushAgentController pushAgentController;

    @PostMapping("/mims/sendPush")
    public String setPayment(){

        return "ok";
    }

    @PostMapping("/mims/sendPushCode")
    public String sendPushCode(){

        return "ok";
    }
}
