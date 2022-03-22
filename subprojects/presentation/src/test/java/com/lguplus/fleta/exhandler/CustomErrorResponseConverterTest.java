package com.lguplus.fleta.exhandler;

import com.lguplus.fleta.data.dto.response.CommonErrorResponseDto;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import com.lguplus.fleta.data.dto.response.ErrorResponseDto;
import com.lguplus.fleta.data.vo.error.ErrorResponseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class CustomErrorResponseConverterTest {

    private CustomErrorResponseConverter customErrorResponseConverter;

    @BeforeEach
    void setUp() throws Exception {
        customErrorResponseConverter = new CustomErrorResponseConverter(ErrorResponseVo.class, "errorResponseBuilder");
    }

    @Test
    void testConvert() throws Throwable {
        ErrorResponseDto error = ErrorResponseDto.builder().flag("9999").message("기타 에러").build();
        CommonResponseDto result = customErrorResponseConverter.convert(error);

        String flag = result.getFlag();
        assertThat(flag).isEqualTo("9999");

    }
}
