package com.lguplus.fleta.repository.latest;

import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestEntity;

import java.util.List;

public interface LatestRepository {
    List<LatestEntity> getLatestList(LatestRequestDto latestRequestDto);

    List<LatestEntity> getLatestCheckList(LatestRequestDto latestRequestDto);

    int deleteLatest(LatestRequestDto latestRequestDto);

    LatestEntity insertLatest(LatestRequestDto latestRequestDto);
}
