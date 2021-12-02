package com.lguplus.fleta.api.outer.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.*;
import com.lguplus.fleta.data.vo.LatestPostRequestVo;
import com.lguplus.fleta.data.vo.LatestSearchRequestVo;
import com.lguplus.fleta.exception.ExceedMaxRequestException;
import com.lguplus.fleta.exception.database.DataAlreadyExistsException;
import com.lguplus.fleta.service.latest.LatestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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
        GenericRecordsetResponseDto<LatestDto> result;
        LatestRequestDto latestRequestDto = vo.convert();
        result = latestService.getLatestList(latestRequestDto);
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
        } catch (Exception e) {
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
    @PostMapping("/smartux/latest")
    public CommonResponseDto insertLatest(@Valid LatestPostRequestVo vo) throws Exception{
        CommonResponseDto result;
        LatestRequestDto latestRequestDto = vo.convert();

        int insertCnt = latestService.insertLatest(latestRequestDto);
        //} catch (Exception e) {
        //D:\space\mims-asis\MIMS\smartux\src\main\java\com\dmi\smartux\common\exception 참조해서 에러처리 완료할것
        result = SuccessResponseDto.builder().build();
        return result;
    }

}
