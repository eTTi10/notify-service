package com.lguplus.fleta.repository;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestEntity;

import java.util.List;

public interface LatestRepository {
    List<LatestDto> getLatestList(LatestRequestDto latestRequestDto);
    List<LatestDto> getLatestCheckList(LatestRequestDto latestRequestDto);
    int deleteLatest(LatestRequestDto latestRequestDto);
    int insertLatest(LatestRequestDto latestRequestDto);
}