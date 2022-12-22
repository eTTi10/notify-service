package com.lguplus.fleta.repository.latest;

import com.lguplus.fleta.data.dto.request.outer.MobileLatestRequestDto;
import com.lguplus.fleta.data.entity.MobileLatest;
import java.util.List;

public interface MobileLatestRepository {

    List<MobileLatest> getLatestCountList(MobileLatestRequestDto requestDto);

    MobileLatest insertLatest(MobileLatestRequestDto requestDto);
}
