package com.lguplus.fleta.api.outer.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.*;
import com.lguplus.fleta.data.vo.LatestPostRequestVo;
import com.lguplus.fleta.data.vo.LatestSearchRequestVo;
import com.lguplus.fleta.service.latest.LatestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Locale;

/**
 * 최신회 Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LatestController {

    private final LatestService latestService;

    /**
     * 최신회 알림 조회 API
     * @param vo
     * sa_id 가입자 번호
     * stb_mac 가입자 맥 어드레스
     * ctn 전화번호
     * cat_id 카테고리 아이디
     * @return 최신회 알림 리스트
     * @throws Exception
     * 기준참조 - programming-service\subprojects\presentation\src\main\java\com\lguplus\fleta\api\outer\menunoti
     */
    @GetMapping(value = "/smartux/latest")
    public GenericRecordsetResponseDto<LatestDto> getNoticeInfoApi(@Valid LatestSearchRequestVo vo) throws Exception{
        //ASIS확인검토필요 - SmartUXException exception = new SmartUXException();
        //ASIS확인검토필요 - CLog cLog = new CLog(LogFactory.getLog(TAG), request);
        GenericRecordsetResponseDto<LatestDto> result;
        try{
            log.info(vo.getSaId(), vo.getMac(), vo.getCtn(), vo.getCatId());
            LatestRequestDto latestRequestDto = vo.convert();
            result = latestService.getLatest(latestRequestDto);
            //ASIS확인검토필요 - } catch (SmartUXException e) {
        } catch (Exception e) {
            //ASIS확인검토필요
            throw e;
        }
        return result;
    }


    /**
     * 최신회 알림 삭제 API
     * @param vo
     * sa_id 가입자 번호
     * stb_mac 가입자 맥 어드레스
     * ctn 전화번호
     * cat_id 카테고리 아이디
     * @return 성공여부&메세지
     * @throws Exception
     * 기준참조 - none
     */
    @DeleteMapping("/smartux/latest")
    public CommonResponseDto deleteLatest(@Valid LatestSearchRequestVo vo) throws Exception{
        //ASIS확인검토 - SmartUXException exception = new SmartUXException();
        //ASIS확인검토 - CLog cLog = new CLog(LogFactory.getLog(TAG), request);
        log.info("/latest deleteLatest()");
        CommonResponseDto result;

        try {
            log.info(vo.getSaId(), vo.getMac(), vo.getCtn(), vo.getCatId());

            LatestRequestDto latestRequestDto = vo.convert();
            int deleteCnt = latestService.deleteLatest(latestRequestDto);

            if (0 < deleteCnt) {
                //ASIS확인검토필요
                result = SuccessResponseDto.builder().build();
            } else {
                //ASIS확인검토필요
                throw new Exception("flag:flag.deleteNotFound, message:message.deleteNotFound");
            }
            //ASIS확인검토 - } catch (SmartUXException e) {
        } catch (Exception e) {
            //ASIS확인검토필요
            result = ErrorResponseDto.builder().build();
            throw e;
        }
        //ASIS확인검토필요 - cLog.endLog(stb_mac, sa_id, ctn, cat_id, result.getFlag());
        return result;
    }


    /**
     * 최신회 알림 등록 API
     * @param vo
     * sa_id 가입자 번호
     * stb_mac 가입자 맥 어드레스
     * ctn 전화번호
     * cat_id 카테고리 아이디
     * cat_name 카테고리명
     * reg_id Push를 위한 아이디
     * @return 성공여부&메세지
     * @throws Exception
     * 기준참조 - none
     */
    @PostMapping("/smartux/latest")
    public CommonResponseDto insertLatest(@Valid LatestPostRequestVo vo) throws Exception{
        //ASIS확인검토 - SmartUXException exception = new SmartUXException();
        //ASIS확인검토 - CLog cLog = new CLog(LogFactory.getLog(TAG), request);
        log.info("/latest deleteLatest()");
        CommonResponseDto result;

        try {
            log.info(vo.getSaId(), vo.getMac(), vo.getCtn(), vo.getCatId(), vo.getCategoryGb());

            LatestRequestDto latestRequestDto = vo.convert();
            /*
            if(StringUtils.isEmpty(latestRequestDto.getCategoryGb())){
                latestRequestDto.setCategoryGb(latestRequestDto.getCategoryGb().toUpperCase());//소문자로 들어오면 대문자로 변환
            }
            */

           // latestRequestDto.setCategoryGb("vvvvvv");//소문자로 들어오면 대문자로 변환

            int insertCnt = latestService.insertLatest(latestRequestDto);

            if (0 < insertCnt) {
                //ASIS확인검토필요
                result = SuccessResponseDto.builder().build();
            } else {
                //ASIS확인검토필요
                throw new Exception("flag:flag.deleteNotFound, message:message.deleteNotFound");
            }
            //ASIS확인검토 - } catch (SmartUXException e) {
        } catch (Exception e) {
            //ASIS확인검토필요
            result = ErrorResponseDto.builder().build();
            throw e;
        }
        //ASIS확인검토필요 - cLog.endLog(stb_mac, sa_id, ctn, cat_id, result.getFlag());
        return result;
    }

}
