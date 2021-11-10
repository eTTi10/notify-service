package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class LatestService {

    private final LatestDomainService latestDomainService;

    public  GenericRecordsetResponseDto<LatestDto> getLatest(LatestRequestDto latestRequestDto) {
        List<LatestDto> result = latestDomainService.getLatest(latestRequestDto);

        return GenericRecordsetResponseDto.<LatestDto>genericRecordsetResponseBuilder()
                .totalCount(result.size())
                .recordset(result)
                .build();
    }


}
