package com.lguplus.fleta.service.push;

import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushAnnounceDomainService {

    private final PushConfig pushConfig;

    //설정 정보 ///////////////
    private int transactionId;
    private String serverIp;
    private int serverPort;
    private String url;
    private int timeout;
    private String header;
    private String protocol;
    private String encoding;
    //////////////////////////

    /**
     * Announcement 푸시등록
     *
     * @param pushRequestAnnounceDto Announcement 푸시등록을 위한 DTO
     * @return Announcement  푸시등록 결과
     */
    public String requestAnnouncement(PushRequestAnnounceDto pushRequestAnnounceDto) {
        log.debug("requestAnnouncement ::::::::::::::: {}", pushRequestAnnounceDto);

        //1. Make Message

        //2. Send Announcement Push

        return null;
    }

    @PostConstruct
    void postConstruct() {
        transactionId = 0;
        serverIp = pushConfig.getCommPropValue("announce.server.ip");
        serverPort = Integer.parseInt(pushConfig.getCommPropValue("announce.server.port"));
        url = pushConfig.getCommPropValue("announce.server.url");
        timeout = Integer.parseInt(pushConfig.getCommPropValue("announce.server.timeout"));
        header = pushConfig.getCommPropValue("announce.server.header");
        protocol = pushConfig.getCommPropValue("announce.server.protocol");
        encoding = pushConfig.getCommPropValue("announce.server.encoding");
    }

}
