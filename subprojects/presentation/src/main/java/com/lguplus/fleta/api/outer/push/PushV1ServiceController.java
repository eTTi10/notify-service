package com.lguplus.fleta.api.outer.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.PushMultiResponseResultDto;
import com.lguplus.fleta.data.dto.response.PushResponseResultDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseMultiDto;
import com.lguplus.fleta.data.mapper.PushRequestMapper;
import com.lguplus.fleta.data.vo.*;
import com.lguplus.fleta.exception.ParameterMissingException;
import com.lguplus.fleta.service.push.PushAnnouncementService;
import com.lguplus.fleta.service.push.PushMultiService;
import com.lguplus.fleta.service.push.PushSingleService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Api(tags = "Push", description = "Push Message 전송 서비스")
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class PushV1ServiceController {

    private final PushAnnouncementService pushAnnouncementService;

    private final PushSingleService pushSingleService;

    private final PushMultiService pushMultiService;

    private final PushRequestMapper pushRequestMapper;

    private final MappingJackson2XmlHttpMessageConverter xmlHttpMessageConverter;

    @Value("${push-comm.push.reject.regList}")
    private String pushRejectRegList;

    /**
     * 단건푸시등록
     *
     * @param
     * @return 단건푸시등록 결과 응답
     */
    @PostMapping(value = "/pushagent/v1/push")
    public PushResponseResultDto pushRequestV1(
            @RequestParam(value="app_id",   required=false) String appId,
            @RequestParam(value="service_id",   required=false) String serviceId,
            @RequestParam(value="push_type",    required=false) String pushType,
            @RequestBody String requestBodyStr
        ) throws JsonProcessingException {

        //Validation
        if(pushType == null || pushType.isEmpty()) {
            pushType = "G";
        }

        if(appId == null || appId.isEmpty()) {
            throw new ParameterMissingException("app_id 파라미터값이 전달이 안됨");
        }

        if(serviceId == null || serviceId.isEmpty()) {
            throw new ParameterMissingException("service_id 파라미터값이 전달이 안됨");
        }

        PushSingleRequestVo pushSingleRequestVo = xmlHttpMessageConverter.getObjectMapper().readValue(requestBodyStr, PushSingleRequestVo.class);
        PushRequestBodySingleVo pushRequestBodySingleVo =  pushSingleRequestVo.convert(appId, serviceId, pushType);
        PushRequestSingleDto pushRequestSingleDto = pushRequestMapper.toDtoSingle(pushRequestBodySingleVo);

        //Reject User
        if (!isValidRegId(pushRequestSingleDto.getRegId())) {
            return PushResponseResultDto.builder().flag("0000").message("성공").build();
        }

        PushClientResponseDto pushClientResponseDto = pushSingleService.requestPushSingle(pushRequestSingleDto);

        return PushResponseResultDto.builder().flag(pushClientResponseDto.getCode()).message(pushClientResponseDto.getMessage()).build();
    }

    /**
     * Multi 푸시등록
     *
     * @return Multi 푸시등록 결과 응답
     */
    @PostMapping(value = "/pushagent/v1/multi")
    public PushMultiResponseResultDto multiPushRequestV1(
            @RequestParam(value="app_id",   required=false) String appId,
            @RequestParam(value="service_id",   required=false) String serviceId,
            @RequestParam(value="multi_count",    required=false) String multiCount,
            @RequestParam(value="push_type",    required=false) String pushType,
            @RequestBody String requestBodyStr
    ) throws JsonProcessingException {

        //Validation
        if(pushType == null || pushType.isEmpty()) {
            pushType = "G";
        }

        if(appId == null || appId.isEmpty()) {
            throw new ParameterMissingException("app_id 파라미터값이 전달이 안됨");
        }

        if(serviceId == null || serviceId.isEmpty()) {
            throw new ParameterMissingException("service_id 파라미터값이 전달이 안됨");
        }

        PushMultiRequestVo pushMultiRequestVo = xmlHttpMessageConverter.getObjectMapper().readValue(requestBodyStr, PushMultiRequestVo.class);
        PushRequestBodyMultiVo pushRequestBodyMultiVo =pushMultiRequestVo.convert(appId, serviceId, pushType);

        //Reject User Filtering
        pushMultiRequestVo.setUsers(pushMultiRequestVo.getUsers().stream().filter(this::isValidRegId).collect(Collectors.toList()));

        PushRequestMultiDto dto = pushRequestMapper.toDtoMulti(pushRequestBodyMultiVo);

        PushClientResponseMultiDto responseMultiDto = pushMultiService.requestMultiPush(dto);

        return PushMultiResponseResultDto.builder().flag(responseMultiDto.getCode()).message(responseMultiDto.getMessage()).failUsers(responseMultiDto.getFailUsers()).build();
    }
    /**
     * 공지 푸시 등록
     *
     * @param
     * @return 공지 푸시 등록 결과 응답
     */
    @PostMapping(value = "/pushagent/v1/announcement")
    public PushResponseResultDto pushRequestAnnouncementV1(
            @RequestParam(value="app_id",   required=false) String appId,
            @RequestParam(value="service_id",   required=false) String serviceId,
            @RequestParam(value="push_type",    required=false) String pushType,
            @RequestBody String requestBodyStr
    ) throws JsonProcessingException {

        //Validation
        if(pushType == null || pushType.isEmpty()) {
            pushType = "G";
        }

        if(appId == null || appId.isEmpty()) {
            throw new ParameterMissingException("app_id 파라미터값이 전달이 안됨");
        }

        if(serviceId == null || serviceId.isEmpty()) {
            throw new ParameterMissingException("service_id 파라미터값이 전달이 안됨");
        }

        PushAnnounceRequestVo pushAnnounceRequestVo = xmlHttpMessageConverter.getObjectMapper().readValue(requestBodyStr, PushAnnounceRequestVo.class);
        PushRequestBodyAnnounceVo pushRequestBodyAnnounceVo =  pushAnnounceRequestVo.convert(appId, serviceId, pushType);
        PushRequestAnnounceDto pushRequestAnnounceDto = pushRequestMapper.toDtoAnnounce(pushRequestBodyAnnounceVo);

        PushClientResponseDto pushClientResponseDto = pushAnnouncementService.requestAnnouncement(pushRequestAnnounceDto);

        return PushResponseResultDto.builder().flag(pushClientResponseDto.getCode()).message(pushClientResponseDto.getMessage()).build();
    }

    private boolean isValidRegId(String regId) {
        return !("|" + this.pushRejectRegList + "|").contains("|" + regId + "|");
    }
}
