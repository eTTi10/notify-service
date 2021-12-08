package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushAnnounceDomainClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.exception.push.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushAnnounceDomainService {

    private final PushConfig pushConfig;
    private final PushAnnounceDomainClient pushAnnounceDomainClient;

    private AtomicInteger _transactionIDNum = new AtomicInteger(0);

    @Value("${push-comm.push.old.lgupush.pushAppId}")
    private String oldLgPushAppId;

    @Value("${push-comm.push.old.lgupush.notiType}")
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
        paramMap.put("msg_id", "PUSH_ANNOUNCEMENT");
        paramMap.put("push_id", getTransactionId());
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getAppId());
        paramMap.put("noti_contents", dto.getMsg());

        String servicePwd = pushConfig.getServicePassword(dto.getServiceId());
        if (servicePwd == null) {
            throw new ServiceIdNotFoundException();
        }
        paramMap.put("service_passwd", servicePwd);

        if("LGUPUSH_OLD".equals(pushConfig.getServiceLinkType(dto.getServiceId()))) {
            paramMap.put("push_app_id", oldLgPushAppId);
            paramMap.put("noti_type", oldLgPushNotiType);
        }

        dto.getItems().forEach(e -> {
            String[] item = e.split("\\!\\^");
            if(item.length >= 2){
                paramMap.put(item[0], item[1]);
            }
        });

        //2. Send Announcement Push
        PushResponseDto pushAnnounceResponseDto = pushAnnounceDomainClient.requestAnnouncement(paramMap);

        //3. Send Result
        String status_code = pushAnnounceResponseDto.getStatusCode();
        String status_msg = pushAnnounceResponseDto.getStatusMsg();
        log.info("[pushAnnouncement][reqAnnouncement] - ["+dto.getAppId()+"]["+dto.getServiceId()+"]["+status_code+"]["+status_msg+"]");

        if(status_code.equals("200")){
            log.info("[pushAnnouncement]["+status_code+"] [SUCCESS]");
        }else{
            log.info("[pushAnnouncement]["+status_code+"] [FAIL]");

            //실패
            switch (status_code) {
                case "202":
                    throw new AcceptedException();
                case "400":
                    throw new BadRequestException();
                case "401":
                    throw new UnAuthorizedException();
                case "403":
                    throw new ForbiddenException();
                case "404":
                    throw new NotFoundException();
                case "410":
                    throw new NotExistRegistIdException();
                case "412":
                    throw new PreConditionFailedException();
                case "500":
                    throw new InternalErrorException();
                case "502":
                    throw new ExceptionOccursException();
                case "503":
                    throw new ServiceUnavailableException();
                case "5102":
                    throw new SocketTimeException();
                case "5103": //FeignException
                    throw new SocketException();
                default:
                    throw new PushEtcException();//("기타 오류"); //9999
            }
        }

        return PushClientResponseDto.builder().build();
    }

    private String getTransactionId() {
        if(_transactionIDNum.get() >= 9999) {
            _transactionIDNum.set(0);
        }
        return DateFormatUtils.format(new Date(), "yyyyMMdd") + String.format("%04d", _transactionIDNum.incrementAndGet());
    }

}
