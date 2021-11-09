package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.type.ImageServerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LatestService {
    public GenericRecordsetResponseDto<LatestService> getLatest(String categoryId, String model) {
        log.debug("getMenuNotiList() - {}:categoryId={}, model={}", "Menu 노티(말풍선) 정보 조회", categoryId, model);

        String imageResizeServerUrl = imageServerDomainService.getImageServerUrl(ImageServerType.SMARTUX_RESIZE); //get 이미지 resize server url
        List<MenuNotiDto> menuNotiList = menuNotiDomainService.getMenuNotiList(imageResizeServerUrl);

        List<MenuNotiDto> result = menuNotiDomainService.getFilteredMenuNotiList(menuNotiList, categoryId, model);

        return GenericRecordsetResponseDto.<MenuNotiDto>genericRecordsetResponseBuilder()
                .totalCount(result.size())
                .recordset(result)
                .build();
    }
}
