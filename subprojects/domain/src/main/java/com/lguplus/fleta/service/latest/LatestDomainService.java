package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.LatestCheckDto;
import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.Latest;
import com.lguplus.fleta.data.mapper.LatestMapper;
import com.lguplus.fleta.exception.ExceedMaxRequestException;
import com.lguplus.fleta.exception.ExtRuntimeException;
import com.lguplus.fleta.exception.database.DataAlreadyExistsException;
import com.lguplus.fleta.exception.database.DatabaseException;
import com.lguplus.fleta.exception.latest.DeleteNotFoundException;
import com.lguplus.fleta.repository.latest.LatestRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class LatestDomainService {

    private static final int MAX_COUNT = 5;
    private final LatestMapper latestMapper;
    private final LatestRepository latestRepository;

    /**
     * 최신회 정보조회
     *
     * @param latestRequestDto 최신회 정보조회를 위한 DTO
     * @return 최신회 정보조회 결과
     */
    public List<LatestDto> getLatestList(LatestRequestDto latestRequestDto) {
        List<Latest> records = latestRepository.getLatestList(latestRequestDto);
        List<LatestDto> resultList = new ArrayList<>();
        records.forEach(e -> {
            LatestDto item = latestMapper.toDto(e);
            resultList.add(item);
        });
        return resultList;
    }


    /**
     * 최대등록개수, 중복체크를 위한 리스트출력
     *
     * @param latestRequestDto 최신회 정보조회를 위한 DTO
     * @return 최신회 정보조회 결과
     */
    public LatestCheckDto getLatestCheckList(LatestRequestDto latestRequestDto) {
        List<Latest> checks = latestRepository.getLatestCheckList(latestRequestDto);

        if (MAX_COUNT <= checks.size()) {
            throw new ExceedMaxRequestException("최대 등록 갯수 초과");
        } else if (checks.stream().anyMatch(item -> item.getCatId().equals(latestRequestDto.getCatId()))) {
            throw new DataAlreadyExistsException("기존 데이터 존재");
        }

        return LatestCheckDto.builder().code(LatestCheckDto.SUCCESS_CODE).build();
    }

    /**
     * 최신회 정보삭제
     *
     * @param latestRequestDto 최신회 정보삭제를 위한 DTO                                                                                                                                                                                 +
     * @return 삭제건수
     */
    public int deleteLatest(LatestRequestDto latestRequestDto) {
        int deleteCount = latestRepository.deleteLatest(latestRequestDto);

        if (0 >= deleteCount) {
            throw new DeleteNotFoundException("삭제대상 없음");
        } else {
            return deleteCount;
        }
    }

    /**
     * 최신회 정보등록
     *
     * @param latestRequestDto 최신회 정보등록을 위한 DTO
     * @return 등록건수
     */
    public void insertLatest(LatestRequestDto latestRequestDto) {
        getLatestCheckList(latestRequestDto);
        try {
            latestRepository.insertLatest(latestRequestDto);
        } catch (BadSqlGrammarException | UncategorizedSQLException | DataIntegrityViolationException e) {
            throw new DatabaseException();
        } catch (Exception e) {
            throw new ExtRuntimeException();
        }
    }

}
