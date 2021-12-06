package com.lguplus.fleta.api.outer.latest;

import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.mapper.LatestPostRequestMapper;
import com.lguplus.fleta.data.mapper.LatestSearchRequestMapper;
import com.lguplus.fleta.data.vo.LatestPostRequestVo;
import com.lguplus.fleta.data.vo.LatestSearchRequestVo;
import com.lguplus.fleta.service.latest.LatestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 최신회 Controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LatestController {
    private final LatestSearchRequestMapper latestSearchRequestMapper;
    private final LatestPostRequestMapper latestPostRequestMapper;

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
     */
    @GetMapping(value = "/smartux/latest")
    public GenericRecordsetResponseDto<LatestDto> getLatestList(@Valid LatestSearchRequestVo vo) throws Exception{
        GenericRecordsetResponseDto<LatestDto> result;
        LatestRequestDto latestRequestDto = latestSearchRequestMapper.toDto(vo);
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
        LatestRequestDto latestRequestDto = latestSearchRequestMapper.toDto(vo);
        int deleteCnt = latestService.deleteLatest(latestRequestDto);
        return SuccessResponseDto.builder().build();
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
        LatestRequestDto latestRequestDto = latestPostRequestMapper.toDto(vo);
        latestService.insertLatest(latestRequestDto);
        return SuccessResponseDto.builder().build();
    }

}
