package com.lguplus.fleta.service.latest;

import com.lguplus.fleta.data.dto.request.outer.MobileLatestRequestDto;
import com.lguplus.fleta.data.entity.MobileLatest;
import com.lguplus.fleta.exception.ExceedMaxRequestException;
import com.lguplus.fleta.exception.ExtRuntimeException;
import com.lguplus.fleta.exception.database.DataAlreadyExistsException;
import com.lguplus.fleta.repository.latest.MobileLatestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MobileLatestDomainService {


    private static final int MAX_COUNT = 5;

    private final MobileLatestRepository mobileLatestRepository;

    public void insertLatest(MobileLatestRequestDto requestDto) {

        checkLatestCountList(requestDto);

        try {
            mobileLatestRepository.insertLatest(requestDto);
        } catch (Exception e) {
            // todo : BadSqlGrammarException | UncategorizedSQLException | DataIntegrityViolationException 일 때
            // todo : throw new DatabaseException();
            throw new ExtRuntimeException();
        }
    }

    private void checkLatestCountList(MobileLatestRequestDto requestDto) {

        List<MobileLatest> latestCountList = mobileLatestRepository.getLatestCountList(requestDto);

        if (MAX_COUNT <= latestCountList.size()) {
            throw new ExceedMaxRequestException("최대 등록 갯수 초과");
        } else if (latestCountList.stream().anyMatch(item -> item.getCatId().equals(requestDto.getCatId()))) {
            throw new DataAlreadyExistsException("기존 데이터 존재");
        }
    }
}
