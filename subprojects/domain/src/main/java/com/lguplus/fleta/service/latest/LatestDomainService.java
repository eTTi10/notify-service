package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestCheckDto;
import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestCheckEntity;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.data.mapper.LatestMapper;
import com.lguplus.fleta.exception.ExceedMaxRequestException;
import com.lguplus.fleta.exception.database.DatabaseException;
import com.lguplus.fleta.exception.database.DuplicateKeyException;
import com.lguplus.fleta.exception.latest.DeleteNotFoundException;
import com.lguplus.fleta.exception.latest.JpaSocketException;
import com.lguplus.fleta.repository.LatestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LatestDomainService {
    private final LatestMapper latestMapper;
    private final LatestRepository latestRepository;

    @Value("${latestroot.latest.maxCount}")
    private int maxCnt;

    /**
     * 최신회 정보조회
     * @param latestRequestDto  최신회 정보조회를 위한 DTO
     * @return 최신회 정보조회 결과
     */
    public List<LatestDto> getLatestList(LatestRequestDto latestRequestDto) {
        List<LatestEntity> rs = latestRepository.getLatestList(latestRequestDto);
        List<LatestDto> resultList = new ArrayList<LatestDto>();
        rs.forEach(e->{
            LatestDto item = latestMapper.toDto(e);
            resultList.add(item);
        });
        return resultList;
    }


    /**
     * 최대증록개수, 중복체크를 위한 리스트출력
     * @param latestRequestDto  최신회 정보조회를 위한 DTO
     * @return 최신회 정보조회 결과
     */
    public LatestCheckDto getLatestCheckList(LatestRequestDto latestRequestDto) {
        List<LatestCheckEntity> checkList = latestRepository.getLatestCheckList(latestRequestDto);
        LatestCheckDto resultLatestCheckDto = new LatestCheckDto();

        resultLatestCheckDto.setCode(resultLatestCheckDto.SUCCESS_CODE);

        if (checkList.stream().anyMatch(item -> item.getCatId().equals(latestRequestDto.getCatId()))) {
            throw new DuplicateKeyException("기존 데이터 존재");//1201;//중복

        }else if(maxCnt < checkList.size()) {
            throw new ExceedMaxRequestException("최대 등록 갯수 초과");//최대값 초과
        }
        return resultLatestCheckDto;
    }

    /**
     * 최신회 정보삭제
     * @param latestRequestDto 최신회 정보삭제를 위한 DTO                                                                                                                                                                                 +
     * @return 삭제건수
     */
    public int deleteLatest(LatestRequestDto latestRequestDto) {
        int deleteCnt = latestRepository.deleteLatest(latestRequestDto);
//        try {
            if (0 >= deleteCnt) {
                throw new DeleteNotFoundException("삭제대상없음");//1410
            } else {
                return deleteCnt;
            }
//        } catch (Exception e) {
//            throw new RuntimeException();
//        }
    }

    /**
     * 최신회 정보등록
     * @param latestRequestDto 최신회 정보등록을 위한 DTO
     * @return 등록건수
     */
    public int insertLatest(LatestRequestDto latestRequestDto) {
        int insertCnt = 0;
        LatestCheckDto check = getLatestCheckList(latestRequestDto);

        try {
            insertCnt = latestRepository.insertLatest(latestRequestDto);
        }catch(Exception e){
            if(e instanceof BadSqlGrammarException){
                throw new DatabaseException();//8999 DB에러
            }else{
                throw new RuntimeException();
            }
        }
        return insertCnt;
    }

}
