package com.lguplus.fleta.service.httppush;

import com.lguplus.fleta.data.dto.request.inner.HttpPushSingleRequestDto;
import com.lguplus.fleta.data.dto.request.inner.HttpPushRequestDto;
import com.lguplus.fleta.data.dto.response.inner.DeviceInfosResponseDto;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.exception.database.DataNotExistsInnerException;
import com.lguplus.fleta.service.push.DeviceInfoDomainService;
import com.lguplus.fleta.service.send.PushDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Http SinglePush Service
 * <p>
 * 단건 푸시등록
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HttpSinglePushService {

    private final HttpSinglePushDomainService httpSinglePushDomainService;

    private final DeviceInfoDomainService deviceInfoDomainService;

    private final PushDomainService pushDomainService;


    /**
     * 단건푸시등록
     *
     * @param httpPushSingleRequestDto 단건푸시등록을 위한 DTO
     * @return 단건푸시등록 결과
     */
    public HttpPushResponseDto requestHttpPushSingle(HttpPushSingleRequestDto httpPushSingleRequestDto) {
        return httpSinglePushDomainService.requestHttpPushSingle(httpPushSingleRequestDto);
    }

    /**
     * 단말(G:안드로이드, A:아이폰) 종류에 따른 푸시 발송
     * @param httpPushRequestDto
     * @return
     */

    public HttpPushResponseDto getDeviceInfosAndPushRequest(HttpPushRequestDto httpPushRequestDto) {
        HttpPushResponseDto httpPushResponseDto = HttpPushResponseDto.builder().build();
        // 1. 가입자 단말 종류 조회
        List<DeviceInfosResponseDto> deviceInfos = deviceInfoDomainService.getDeviceInfos(httpPushRequestDto);

        if(deviceInfos.isEmpty()){
            throw new DataNotExistsInnerException();
        }

        // 2. 푸시 발송
        for(DeviceInfosResponseDto deviceInfo :deviceInfos){
            // 푸시 전문 dto 생성
            HttpPushSingleRequestDto pushServiceInfo = pushDomainService.getPushServiceInfo(httpPushRequestDto, deviceInfo.getAgentType());
            // 푸시 발송
            httpPushResponseDto = httpSinglePushDomainService.requestHttpPushSingle(pushServiceInfo);
        }
        return httpPushResponseDto;
    }

}
