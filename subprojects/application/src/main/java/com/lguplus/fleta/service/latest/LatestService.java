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
        int result = latestDomainService.deleteLatest(latestRequestDto);
        return result;
    }


    /**
     * 최신회 등록
     * @param latestRequestDto
     * @return int
     */
    public  int insertLatest(LatestRequestDto latestRequestDto) {
        int result = latestDomainService.insertLatest(latestRequestDto);
        return result;
    }
}
