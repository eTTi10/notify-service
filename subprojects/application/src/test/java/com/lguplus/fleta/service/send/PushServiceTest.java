package com.lguplus.fleta.service.send;

import com.lguplus.fleta.data.dto.request.outer.SendPushCodeRequestDto;
import com.lguplus.fleta.data.dto.response.PushServiceResultDto;
import com.lguplus.fleta.data.dto.response.SendPushResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class PushServiceTest {

    String sFlag = "0000";
    String sMessage = "성공";

    @InjectMocks
    PushService pushService;

    @Mock
    PushDomainService pushDomainService;

    @Test
    @DisplayName("정상적인 응답을 하는지 테스트")
    void sendPushCode() {

        //given
        List<PushServiceResultDto> pushServiceResultDtoArrayList = new ArrayList<>();

        PushServiceResultDto pushServiceResultDto = PushServiceResultDto.builder()
                .sType("H")
                .sFlag(sFlag)
                .sMessage(sMessage)
                .build();

        pushServiceResultDtoArrayList.add(pushServiceResultDto);

        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
                .message(sMessage)
                .service(pushServiceResultDtoArrayList)
                .build();


        given(pushDomainService.sendPushCode(any())).willReturn(sendPushResponseDto);

        //when
        List items = new ArrayList();

        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        Map<String, String> reserveMap = Map.of("address","111111");

        SendPushCodeRequestDto sendPushCodeRequestDto = sendPushCodeRequestDto = SendPushCodeRequestDto.builder()
                .registrationId("M00020200205")
                .pushType("G|A|L")
                .sendCode("P001")
                .regType("1")
                .serviceType("C|TV")
                .reserve(reserveMap)
                .items(items)
                .build();

        //then
        SendPushResponseDto responseDto = pushService.sendPushCode(sendPushCodeRequestDto);
        assertThat(responseDto.getMessage().equals(sendPushResponseDto.getMessage()));
    }
}