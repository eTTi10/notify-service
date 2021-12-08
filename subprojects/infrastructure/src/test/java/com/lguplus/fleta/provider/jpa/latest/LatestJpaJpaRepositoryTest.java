package com.lguplus.fleta.provider.jpa.latest;

import com.lguplus.fleta.config.InfrastructureConfig;
import com.lguplus.fleta.data.dto.request.outer.LatestRequestDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.repository.LatestRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(InfrastructureConfig.class)
@Slf4j
class LatestJpaJpaRepositoryTest {

    @Autowired
    private LatestRepository latestRepository;

    @Test
    @DisplayName("LatestJpaJpaRepositoryTest.getLatestList 정상적으로 리스트 데이터를 수신하는지 확인")
    void getLatestList() {
        // Mock Object
        LatestRequestDto latestRequestDto = LatestRequestDto.builder()
                .saId("500058151453")
                .mac("001c.627e.039c")
                .ctn("01055805424")
                .catId("T3021").build();
        log.info("1111111111");
        List<LatestEntity> responseList = latestRepository.getLatestList(latestRequestDto);

        assertThat(responseList.size()).isGreaterThan(0);   // 결과 리스트가 있는지 확인
        log.info("LatestJpaJpaRepositoryTest.getLatestList End");
    }

}