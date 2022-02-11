package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
//@Transactional(readOnly = true) <== 리뷰에서 어노테이션 추가요청을 받았지만 insertLatest인서트가 되지 않아서 일단 주석처리함
public class LatestService {

    private final LatestDomainService latestDomainService;

    public GenericRecordsetResponseDto<LatestDto> getLatestList(LatestRequestDto latestRequestDto) {
        List<LatestDto> result = latestDomainService.getLatestList(latestRequestDto);
        return GenericRecordsetResponseDto.<LatestDto>genericRecordsetResponseBuilder()
                .totalCount(result.size())
                .recordset(result)
                .build();
    }

    public  int deleteLatest(LatestRequestDto latestRequestDto) {
        return latestDomainService.deleteLatest(latestRequestDto);
    }

    public  void insertLatest(LatestRequestDto latestRequestDto) {
        latestDomainService.insertLatest(latestRequestDto);
    }
}