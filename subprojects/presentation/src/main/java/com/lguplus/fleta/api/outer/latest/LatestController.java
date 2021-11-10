package com.lguplus.fleta.api.outer.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.data.vo.LatestRequestVo;
import com.lguplus.fleta.service.latest.LatestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 최신회 Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LatestController {

    private final LatestService latestService;

    //programming-service\subprojects\presentation\src\main\java\com\lguplus\fleta\api\outer\menunoti

    /**
     * @return 최신회 알림 리스트
     * @param vo
     * [Request]
     * sa_id 가입자 번호
     * stb_mac 가입자 맥 어드레스
     * ctn 전화번호
     * cat_id 카테고리 아이디
     * @return
     */
    @GetMapping(value = "/smartux/latest")
    public GenericRecordsetResponseDto<LatestDto> getNoticeInfoApi(@Valid LatestRequestVo vo) {
        try{
            log.info(vo.getSaId(), vo.getMac(), vo.getCtn(), vo.getCatId());
            // 검색) isEmpty유효성 검사는 @Valid로 대체
            // 등록, 삭제) isNumeric(ctn) 검사는 @Valid로 대체
            // 등록, 삭제) isNull(category_gb, "I20").toUpperCase()
        //} catch (SmartUXException e) { <-- asis확인할것
        } catch (Exception e) {
            log.error(vo.getSaId(), vo.getMac(), vo.getCtn(), vo.getCatId(), e.getClass().getSimpleName(), e.getMessage());
            //asis --> com.dmi.smartux.common.exception.ExceptionHandler handler = new com.dmi.smartux.common.exception.ExceptionHandler(e);
            //asis --> exception.setFlag(handler.getFlag());
            //asis --> exception.setMessage(handler.getMessage());
            throw e;
        }
        LatestRequestDto latestRequestDto = vo.convert();
        GenericRecordsetResponseDto<LatestDto> result = latestService.getLatest(latestRequestDto);
        return result;
    }

    @DeleteMapping("/latest")
    public void deleteLatest(){
        log.info("/latest deleteLatest()");
    }

    @PostMapping("/latest")
    public void insertLatest(String aa){
        log.info("/latest insertLatest()");
    }
}
