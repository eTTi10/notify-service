package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.request.outer.MobileLatestRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MobileLatestService {

    private final MobileLatestDomainService mobileLatestDomainService;

    @Transactional
    public void insertLatest(MobileLatestRequestDto requestDto) {
        mobileLatestDomainService.insertLatest(requestDto);
    }

    @Transactional
    public void deleteLatest(MobileLatestRequestDto requestDto) {
        mobileLatestDomainService.deleteLatest(requestDto);
    }
}
