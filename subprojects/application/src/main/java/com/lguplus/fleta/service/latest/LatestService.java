package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class LatestService {

    private final LatestDomainService latestDomainService;

    public  List<LatestEntity> getLatest(LatestRequestDto latestRequestDto) {
        return latestDomainService.getLatest(latestRequestDto);
    }


}
