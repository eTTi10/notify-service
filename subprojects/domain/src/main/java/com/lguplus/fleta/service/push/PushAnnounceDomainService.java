package com.lguplus.fleta.service.push;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.client.PushAnnounceDomainClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.response.inner.PushAnnounceResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.exception.push.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushAnnounceDomainService {

    private final PushConfig pushConfig;
    private final PushAnnounceDomainClient pushAnnounceDomainClient;
    //private final ObjectMapper objectMapper = new ObjectMapper();

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
        paramMap.put("service_passwd", getServicePwd(dto.getServiceId()));
        paramMap.put("app_id", dto.getAppId());
        paramMap.put("noti_contents", dto.getMsg());

        //구버전 LGUPUSH
        if("LGUPUSH_OLD".equals(getLinkType(dto.getServiceId()))) {
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
        PushAnnounceResponseDto pushAnnounceResponseDto = pushAnnounceDomainClient.requestAnnouncement(paramMap);

        //3. Send Result
        String status_code = pushAnnounceResponseDto.getResponseAnnouncement().getStatusCode();
        String status_msg = pushAnnounceResponseDto.getResponseAnnouncement().getStatusMsg();
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
                default:
                    throw new RuntimeException("기타 오류"); //9999
            }
        }

        return PushClientResponseDto.builder().build();
    }

    // Service Password
    private String getServicePwd(String serviceId) {

        String servicePwd = pushConfig.getServicePropValue(serviceId + ".push.service_pwd");

        if (servicePwd == null) {
            throw new ServiceIdNotFoundException();
        }

        // service_pwd : SHA512 암호화
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(servicePwd.getBytes(StandardCharsets.UTF_8));
            return String.format("%0128x", new BigInteger(1, digest.digest()));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("기타 오류");
        }
    }

    // Link Type
    private String getLinkType(String serviceId) {
        String linkType = pushConfig.getServicePropValue(serviceId + ".push.linkage_type");
        return linkType == null ? "" : linkType;
    }

    private String getTransactionId() {
        if(_transactionIDNum.get() >= 9999) {
            _transactionIDNum.set(0);
        }
        return DateFormatUtils.format(new Date(), "yyyyMMdd") + String.format("%04d", _transactionIDNum.incrementAndGet());
    }

}
