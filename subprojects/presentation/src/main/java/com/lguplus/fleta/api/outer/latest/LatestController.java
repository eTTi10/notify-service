package com.lguplus.fleta.api.outer.latest;

import com.lguplus.fleta.data.dto.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.dto.response.inner.InnerResponseDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.data.vo.LatestRequestVo;
import com.lguplus.fleta.service.latest.LatestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 최신회 Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LatestController {

    private final LatestService latestService;
    /*
        @GetMapping("/latest")
        public List<LatestEntity> getLatest(
                @RequestParam(value="sa_id", required = false) String saId,
                @RequestParam(value="stb_mac", required = false) String stbMac,
                @RequestParam(value="ctn", required = false) String ctn,
                @RequestParam(value="cat_id", required = false) String catId,
                @RequestBody @Valid LatestRequestVo latestRequestVo
        ) {
            LatestRequestDto latestRequestDto = latestRequestVo.convert();
            return latestService.getLatest(latestRequestDto);
        }
    */
    @GetMapping("/latest")
    public List<LatestEntity> getLatest(
            @RequestParam String sa_id,
            @RequestParam String stb_mac,
            @RequestParam String ctn,
            @RequestParam String cat_id
    ) {
        LatestRequestVo latestRequestVo = new LatestRequestVo();
        LatestRequestDto latestRequestDto = latestRequestVo.convert();
        return latestService.getLatest(latestRequestDto);
    }

    @GetMapping("/latest2")
    public List<LatestEntity> getLatest2(
            @RequestParam String sa_id,
            @RequestParam String stb_mac,
            @RequestParam String ctn,
            @RequestParam String cat_id
    ) {
        LatestRequestVo latestRequestVo = new LatestRequestVo();
        LatestRequestDto latestRequestDto = latestRequestVo.convert();
        return latestService.getLatest(latestRequestDto);
    }

/*
{
    "result": {
        "flag": "0000",
        "message": "성공",
        "total_count": "3",
        "list": [
            {
                "album_id": "M0115CF222PPV00",
                "play_time": "3662",
                "mall_cd": "30",
                "price_type_cd": "",
                "good_no": "1511092745",
                "good_nm": "[경품] 광주 천현한우 30일숙성 등심 500g",
                "app_pkg_nm": "uplus",
                "category_nm": "",
                "sale_price": "0",
                "good_img": "http://imagevshop.uplus.co.kr/upload/C14001/goods/745/1511092745_0000002.jpg",
                "web_link_url": "http://mecs.uplus.co.kr",
                "coupon_cont": "",
                "sellpnt_cont": ""
            },
            {
                "album_id": "M0115CF222PPV00",
                "play_time": "3662",
                "mall_cd": "30",
                "price_type_cd": "",
                "good_no": "1507092513",
                "good_nm": "유플러스 테스트 상품 3",
                "app_pkg_nm": "uplus",
                "category_nm": "",
                "sale_price": "0",
                "good_img": "http://imagevshop.uplus.co.kr/upload/C14001/goods/513/1507092513_0000001.jpg",
                "web_link_url": "http://mecs.uplus.co.kr",
                "coupon_cont": "",
                "sellpnt_cont": ""
            }
        ]
    }
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
