package com.lguplus.fleta.service.push;

import com.lguplus.fleta.data.dto.request.inner.PushRequestAnnounceDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushAnnouncementService {

    private final PushAnnounceDomainService pushAnnounceDomainService;

    /**
     * Announcement 푸시등록
     *
     * @param pushRequestAnnounceDto Announcement 푸시등록을 위한 DTO
     * @return 푸시등록 결과
     */
    public PushClientResponseDto requestAnnouncement(PushRequestAnnounceDto pushRequestAnnounceDto) {
        return pushAnnounceDomainService.requestAnnouncement(pushRequestAnnounceDto);
    }

}
