package com.lguplus.fleta.repository.latest;

import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.Latest;
import java.util.List;

public interface LatestRepository {

    List<Latest> getLatestList(LatestRequestDto latestRequestDto);

    List<Latest> getLatestCheckList(LatestRequestDto latestRequestDto);

    int deleteLatest(LatestRequestDto latestRequestDto);

    Latest insertLatest(LatestRequestDto latestRequestDto);
}
