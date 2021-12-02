package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestCheckDto;
import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.exception.latest.DeleteNotFoundException;
import com.lguplus.fleta.repository.LatestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LatestDomainService {
    private final LatestRepository latestRepository;
    private final LatestCheckDto resultLatestCheckDto;

    /**
     * 최신회 정보조회
     * @param latestRequestDto  최신회 정보조회를 위한 DTO
     * @return 최신회 정보조회 결과
     */
    public List<LatestDto> getLatestList(LatestRequestDto latestRequestDto) {
        return latestRepository.getLatestList(latestRequestDto);
    }


    /**
     * 최대증록개수, 중복체크를 위한 리스트출력
     * @param latestRequestDto  최신회 정보조회를 위한 DTO
     * @return 최신회 정보조회 결과
     */
    public LatestCheckDto getLatestCheckList(LatestRequestDto latestRequestDto) {
        List<LatestDto> checkList = latestRepository.getLatestCheckList(latestRequestDto);
        resultLatestCheckDto.setCode(resultLatestCheckDto.SUCCESS_CODE);
        int maxCount = 5;//yml 속성에서 가져올 것 latest.maxCount = 5 속성에 값이 없다면 기본은 5
        if(maxCount < checkList.size())resultLatestCheckDto.setCode(resultLatestCheckDto.OVER_CODE);//최대값 초과
        if (checkList.stream().anyMatch(item -> item.getCatId().equals(latestRequestDto.getCatId()))) {
            resultLatestCheckDto.setCode(resultLatestCheckDto.DUPL_CODE);//중복
        }
        return resultLatestCheckDto;
    }

    /**
     * 최신회 정보삭제
     * @param latestRequestDto 최신회 정보삭제를 위한 DTO
     * @return 삭제건수
     */
    public int deleteLatest(LatestRequestDto latestRequestDto) {
        int deleteCnt = latestRepository.deleteLatest(latestRequestDto);
        if (0 >= deleteCnt) {
            throw new DeleteNotFoundException("삭제대상없음");//1410
        }
        return latestRepository.deleteLatest(latestRequestDto);
    }

    /**
     * 최신회 정보등록
     * @param latestRequestDto 최신회 정보등록을 위한 DTO
     * @return 등록건수
     */
    public int insertLatest(LatestRequestDto latestRequestDto) {
        LatestCheckDto check = getLatestCheckList(latestRequestDto);
        if(check.DUPL_CODE.equals(check.getCode())) {};//1201 최대 등록 갯수 초과
        if(check.OVER_CODE.equals(check.getCode())) {};//8001 기존 데이터 존재
        int insertCnt = latestRepository.insertLatest(latestRequestDto);
        return insertCnt;
    }
}
