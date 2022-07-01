package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class LatestService {

    private final LatestDomainService latestDomainService;

    public GenericRecordsetResponseDto<LatestDto> getLatestList(LatestRequestDto latestRequestDto) {
        List<LatestDto> result = latestDomainService.getLatestList(latestRequestDto);
        return GenericRecordsetResponseDto.<LatestDto>genericRecordsetResponseBuilder()
            .totalCount(result.size())
            .recordset(result)
            .build();
    }

    @Transactional
    public int deleteLatest(LatestRequestDto latestRequestDto) {
        return latestDomainService.deleteLatest(latestRequestDto);
    }

    @Transactional
    public void insertLatest(LatestRequestDto latestRequestDto) {
        latestDomainService.insertLatest(latestRequestDto);
    }
}