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
    public GenericRecordsetResponseDto<LatestDto> getLatestList(@Valid LatestSearchRequestVo vo) throws Exception{
        //ASIS확인검토필요 - SmartUXException exception = new SmartUXException();
        //ASIS확인검토필요 - CLog cLog = new CLog(LogFactory.getLog(TAG), request);
        GenericRecordsetResponseDto<LatestDto> result;
        try{
            log.info(vo.getSaId(), vo.getMac(), vo.getCtn(), vo.getCatId());
            LatestRequestDto latestRequestDto = vo.convert();
            result = latestService.getLatestList(latestRequestDto);
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
                throw new Exception("삭제된 항목이 없습니다. flag:flag.deleteNotFound, message:message.deleteNotFound");
                //throw new Exception("flag:flag.deleteNotFound, message:message.deleteNotFound");
            }
        /*
        ASIS확인검토
        } catch (SmartUXException e) {
            log.error(vo.getSaId(), vo.getMac(), vo.getCtn(), vo.getCatId(), e.getFlag());
            throw e;
         */
        } catch (Exception e) {
            log.error(vo.getSaId(), vo.getMac(), vo.getCtn(), vo.getCatId(), e.getClass().getSimpleName(), e.getMessage());
            /*
            ASIS확인검토필요
            com.dmi.smartux.common.exception.ExceptionHandler handler = new com.dmi.smartux.common.exception.ExceptionHandler(e);
			exception.setFlag(handler.getFlag());
			exception.setMessage(handler.getMessage());
			throw exception;
            */
            result = ErrorResponseDto.builder().build();
            throw e;
        }
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
     */
/*
요구사항)
1) isEmpty체크 - [sa_id, stb_mac, ctn, cat_id, cat_name, reg_id, category_gb]
   return [flag.paramnotfound, message.paramnotfound, entry.getKey()]
2) isNumeric체크 - [ctn]
   return [flag.numberformat, message.numberformat, ctn]
3) String maxCountStr = SmartUXProperties.getProperty("latest.maxCount")
   maxCountStr값을 가져와서 숫자가 아닐경우 기본값 5를 할당

	<!--  중복 및 최대 등록 가능 체크를 위한 리스트 조회 -->
	<select id="checkLatestList" parameterClass="latestVO" resultClass="String" >
		SELECT
			CAT_ID
		FROM
			SMARTUX.PT_UX_LATEST
		WHERE SA_ID=#saID#
		AND MAC=#mac#
		AND CTN=#ctn#
    </select>

    //최대값 초과 체크와 중복값을 체크
    if (checkCount >= maxCount)
        checkLatestList을 가져와서 maxCount보다 크다면 OVER
    else
        for (Object o : list) {
            if (o.toString().equals(latestVO.getCategoryID())) {
                result = "DUPL";//중복항목이 있다면
                break;
            }
        }
    try{
        if ("OVER".equals(checkData)) { // 최대 등록 초과
            에러리턴 ["flag.maxOver", "message.maxOver"]
        } else if ("DUPL".equals(checkData)) { //중복
            에러리턴 ["flag.bedata", "message.bedata"]
        }else{
            ["flag.success", "message.success"]
            저장성공
        }
    } catch (SmartUXException e) {
        에러처리...
		cLog.endLog(stb_mac, sa_id, ctn, cat_id, e.getFlag());
		throw e;
    } catch (Exception e) {
        에러처리...
        cLog.errorLog(stb_mac, sa_id, ctn, cat_id, e.getClass().getSimpleName(), e.getMessage());
        com.dmi.smartux.common.exception.ExceptionHandler handler = new com.dmi.smartux.common.exception.ExceptionHandler(e);
        exception.setFlag(handler.getFlag());
        exception.setMessage(handler.getMessage());
        throw exception;
    }

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

            //중복&최대등록값 체크 [DUPL:중복, "OVER", "SUCCESS"]
            String checkResult = latestService.getLatestCheckList(latestRequestDto);
            log.debug("checkResult========================="+checkResult);
            if("DUPL".equals(checkResult)){
                throw new Exception("중복에러 flag:flag.DUPL, message:message.DUPL");
            }
            if("OVER".equals(checkResult)){
                throw new Exception("등록최대 개수초과 에러 flag:flag.OVER, message:message.OVER");
            }

            int insertCnt = latestService.insertLatest(latestRequestDto);

            if (0 < insertCnt) {
                //ASIS확인검토필요
                result = SuccessResponseDto.builder().build();
            } else {
                //ASIS확인검토필요
                throw new Exception("flag:flag.insertNotFound, message:message.insertNotFound");
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
