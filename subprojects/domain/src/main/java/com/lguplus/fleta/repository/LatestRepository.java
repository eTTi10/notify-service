package com.lguplus.fleta.repository;

import com.lguplus.fleta.data.dto.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestEntity;

import java.util.List;

public interface LatestRepository {
    List<LatestEntity> getLatest(LatestRequestDto latestRequestDto);
}
