package com.lguplus.fleta.service.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiSendDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseMultiDto;
import com.lguplus.fleta.data.dto.response.inner.PushMultiResponseDto;
import com.lguplus.fleta.data.mapper.PushMapper;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushMultiDomainService {

    private final PushConfig pushConfig;
    private final PushMultiClient pushMultiClient;
    private final PushMapper pushMapper;

    @Value("${push-comm.push.old.lgupush.pushAppId}")
    private String oldLgPushAppId;

    @Value("${push-comm.push.old.lgupush.notiType}")
    private String oldLgPushNotiType;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Multi 푸시등록
     *
     * @param dto Multi 푸시등록을 위한 DTO
     * @return Multi 푸시등록 결과
     */
    public PushClientResponseMultiDto requestMultiPush(PushRequestMultiDto dto) {
        //log.trace("requestMultiPush ::::::::::::::: {}", dto)

        String servicePwd = pushConfig.getServicePassword(dto.getServiceId());
        if (servicePwd == null) {
            log.error("ServiceId Not Found:" + dto.getServiceId());
            throw new ServiceIdNotFoundException();
        }

        //Make Message
        PushRequestMultiSendDto multiSendDto = PushRequestMultiSendDto.builder().jsonTemplate(getMessage(dto)).users(dto.getUsers()).build();

        PushMultiResponseDto responseDto = pushMultiClient.requestPushMulti(multiSendDto);

        return pushMapper.toClientResponseDto(responseDto);
    }

    private String getMessage(PushRequestMultiDto dto) {

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("msg_id", PushMultiClient.PUSH_COMMAND);
        paramMap.put("push_id", PushMultiClient.TRANSACT_ID_NM);
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getAppId());
        paramMap.put("noti_contents", dto.getMsg());
        paramMap.put("service_passwd", pushConfig.getServicePassword(dto.getServiceId()));

        if (PushMultiClient.LG_PUSH_OLD.equals(pushConfig.getServiceLinkType(dto.getServiceId()))) {
            paramMap.put("push_app_id", oldLgPushAppId);
            paramMap.put("noti_type", oldLgPushNotiType);
            paramMap.put("regist_id", PushMultiClient.REGIST_ID_NM);
        } else {
            paramMap.put("service_key", PushMultiClient.REGIST_ID_NM);
        }

        dto.getItems().forEach(e -> {
            String[] item = e.split("!\\^");
            if (item.length == 2) {
                paramMap.put(item[0], item[1]);
            }
        });

        ObjectNode oNode = objectMapper.createObjectNode();
        oNode.set("request", objectMapper.valueToTree(paramMap));
        return oNode.toString();
    }

}
