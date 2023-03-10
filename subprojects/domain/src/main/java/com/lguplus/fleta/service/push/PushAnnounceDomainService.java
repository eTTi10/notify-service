package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushAnnounceClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushAnnounceDomainService {

    private static final String DATA_FORMAT = "yyyyMMdd";
    private static final String PUSH_COMMAND = "PUSH_ANNOUNCEMENT";
    private static final int TRANSACTION_MAX_SEQ_NO = 10000;
    private final PushConfig pushConfig;
    private final PushAnnounceClient pushAnnounceClient;
    private final AtomicInteger transactionMsgId = new AtomicInteger(0);

    @Value("${push.gateway.appId}")
    private String oldLgPushAppId;

    @Value("${push.gateway.notiType}")
    private String oldLgPushNotiType;

    /**
     * Announcement 푸시등록
     *
     * @param dto Announcement 푸시등록을 위한 DTO
     * @return Announcement  푸시등록 결과
     */
    public PushClientResponseDto requestAnnouncement(PushRequestAnnounceDto dto) {
        log.debug("requestAnnouncement/PushRequestAnnounceDto ::::::::::::::: {}", dto);

        //1. Make Message
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("msg_id", PUSH_COMMAND);
        paramMap.put("push_id", getTransactionId());
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getApplicationId());
        paramMap.put("noti_contents", dto.getMessage());

        String servicePwd = pushConfig.getServicePassword(dto.getServiceId());
        if (servicePwd == null) {
            throw new ServiceIdNotFoundException();
        }
        paramMap.put("service_passwd", servicePwd);

        if ("LGUPUSH_OLD".equals(pushConfig.getServiceLinkType(dto.getServiceId()))) {
            paramMap.put("push_app_id", oldLgPushAppId);
            paramMap.put("noti_type", oldLgPushNotiType);
        }

        dto.getItems().forEach(e -> paramMap.put(e.getItemKey(), e.getItemValue()));

        //2. Send Announcement Push
        PushResponseDto pushResponseDto = pushAnnounceClient.requestAnnouncement(paramMap);

        //3. Send Result
        String statusCode = pushResponseDto.getStatusCode();
        //String statusMsg = pushResponseDto.getStatusMsg()
        //log.info("[pushAnnouncement][reqAnnouncement] - ["+dto.getApplicationId()+"]["+dto.getServiceId()+"]["+statusCode+"]["+statusMsg+"]")
        log.debug("[pushAnnouncement][" + statusCode + "] [SUCCESS]");

        return PushClientResponseDto.builder().build();
    }

    private String getTransactionId() {
        return DateFormatUtils.format(new Date(), DATA_FORMAT) + String.format("%04d", transactionMsgId.updateAndGet(x -> (x + 1 < TRANSACTION_MAX_SEQ_NO) ? x + 1 : 0));
    }

}
