package com.lguplus.fleta.api.outer.latest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * 최신회 Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LatestController {

  //  private final LatestService latestService;
/*
    @GetMapping("/latest")
    public void getLatest(@ApiIgnore @Valid MenuNotiInfoVo requestVo){
        log.info("/latest getLatest()");
    }
*/
/*

    @GetMapping(value="/smartux/getMenuNoti")
    public GenericRecordsetResponseDto<MenuNotiDto> getMenuNoti(@ApiIgnore @Valid MenuNotiInfoVo requestVo) {
        log.debug("getMenuNoti() - {}:{}", "Menu 노티(말풍선) 정보 조회", ToStringBuilder.reflectionToString(requestVo, ToStringStyle.JSON_STYLE));

        String categoryId = requestVo.getCategoryId();
        String model = requestVo.getModel();
        return menuNotiService.getMenuNotiList(categoryId, model);
    }

*/


    @DeleteMapping("/latest")
    public void deleteLatest(){
        log.info("/latest deleteLatest()");
    }

    @PostMapping("/latest")
    public void insertLatest(String aa){
        log.info("/latest insertLatest()");
    }
}
