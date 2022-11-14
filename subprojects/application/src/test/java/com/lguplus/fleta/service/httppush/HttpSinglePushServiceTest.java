package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushRequestDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.response.inner.DeviceInfosResponseDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import com.lguplus.fleta.exception.database.DataNotExistsInnerException;
import com.lguplus.fleta.service.push.DeviceInfoDomainService;
import com.lguplus.fleta.service.send.PushDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpSinglePushServiceTest {

    private static final String SUCCESS_CODE = "200";

    @InjectMocks
    HttpSinglePushService httpSinglePushService;

    @Mock
    HttpSinglePushDomainService httpSinglePushDomainService;
    @Mock
    DeviceInfoDomainService deviceInfoDomainService;
    @Mock
    PushDomainService pushDomainService;


    @Test
    @DisplayName("정상적으로 단건푸시가 성공하는지 확인")
    void whenRequestSinglePush_thenReturnSuccess() {
        // given
        HttpPushResponseDto httpPushResponseDto = HttpPushResponseDto.builder().build();

        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

        HttpPushSingleRequestDto requestDto = HttpPushSingleRequestDto.builder()
            .applicationId("lguplushdtvgcm")
            .serviceId("30011")
            .pushType("G")
            .users(List.of("01099991234"))
            .message("\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"")
            .build();

        // when
        HttpPushResponseDto responseDto = httpSinglePushService.requestHttpPushSingle(requestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }


    @Test
    @DisplayName("단말 조회 데이터 미존재")
    void getDeviceInfosAndPushRequest_DataNotExists() {
        // given
        List<DeviceInfosResponseDto> deviceInfosResponseDto = new ArrayList<>();

        given(deviceInfoDomainService.getDeviceInfos(any())).willReturn(deviceInfosResponseDto);

        Map<String, String> paramsList = new HashMap<>();
        paramsList.put("service_push_status", "\"Y\"");
        HttpPushRequestDto httpPushRequestDto = HttpPushRequestDto.builder()
                .saId("M14080700169")
                .serviceType("H")
                .items(List.of("\"badge!^1\""))
                .reserve(paramsList)
                .build();

        Exception e = assertThrows(DataNotExistsInnerException.class, () -> httpSinglePushService.getDeviceInfosAndPushRequest(httpPushRequestDto));

        assertTrue(e instanceof DataNotExistsInnerException);



    }
    @Test
    @DisplayName("단말 종류에 따른 푸시 발송 확인")
    void getDeviceInfosAndPushRequest() {
        // given
        List<DeviceInfosResponseDto> deviceInfosResponseDto = new ArrayList<>();
        DeviceInfosResponseDto deviceInfo = DeviceInfosResponseDto.builder()
                .saId("M14080700169")
                .agentType("G")
                .serviceType("H")
                .notiType("")
                .build();
        deviceInfosResponseDto.add(deviceInfo);

        given(deviceInfoDomainService.getDeviceInfos(any())).willReturn(deviceInfosResponseDto);

        // given
        HttpPushSingleRequestDto pushServiceInfo = HttpPushSingleRequestDto.builder()
                .pushType("G")
                .users(List.of("M14080700169"))
                .items(List.of("\"cm!^SERVICE_AGREE|Y\""))
                .message("\"body\":\"알림 설정이 변경되었습니다\"")
                .serviceId("30011")
                .applicationId("lguplushdtvgcm")
                .build();

        given(pushDomainService.getPushServiceInfo(any(), anyString())).willReturn(pushServiceInfo);

        // given
        HttpPushResponseDto httpPushResponseDto = HttpPushResponseDto.builder().build();

        given(httpSinglePushDomainService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

        // when
        Map<String, String> paramsList = new HashMap<>();
        paramsList.put("service_push_status", "\"Y\"");
        HttpPushRequestDto httpPushRequestDto = HttpPushRequestDto.builder()
                .saId("M14080700169")
                .serviceType("H")
                .items(List.of("\"badge!^1\""))
                .reserve(paramsList)
                .build();

        HttpPushResponseDto responseDto = httpSinglePushService.getDeviceInfosAndPushRequest(httpPushRequestDto);

        // then
        assertThat(responseDto.getCode()).isEqualTo(SUCCESS_CODE);   // 성공 코드가 맞는지 확인
    }


}