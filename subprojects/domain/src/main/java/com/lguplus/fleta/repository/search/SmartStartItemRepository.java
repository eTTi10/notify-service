package com.lguplus.fleta.repository.search;

import com.lguplus.fleta.data.dto.request.SmartStartItemRequestDto;
import com.lguplus.fleta.data.entity.SmartStartItemInfoEntity;

import java.util.List;

public interface SmartStartItemRepository {

    List<SmartStartItemInfoEntity> getSmartStartItemList(SmartStartItemRequestDto request);

}
