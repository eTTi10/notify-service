package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.BootConfig;
import com.lguplus.fleta.data.type.response.InnerResponseCodeType;
import com.lguplus.fleta.data.type.response.InnerResponseErrorType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {BootConfig.class})
@DisplayName("HTTP API 응답")
class InnerResponseDtoTest {

    @Autowired
    ObjectMapper objectMapper;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    private static class TestDto {

        private int id;
        private String name;
    }

    @Test
    @DisplayName("Response 객체 - No Result")
    void basicResponse() {
        //given
        InnerResponseDto<Object> responseDto = InnerResponseDto.of(InnerResponseCodeType.OK);

        //when
        String json = null;
        try {
            json = objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //then
        System.out.println("===> json = " + json);
        assertThat(json).contains("code");
        assertThat(json).contains("0000");
        assertThat(json).contains("message");
        assertThat(json).contains("정상");
        assertThat(json).doesNotContain("result");
        assertThat(json).doesNotContain("errors");
    }

    @Test
    @DisplayName("Response 객체 - SINGLE Result")
    void singleResultResponse() {
        //given
        TestDto testDto = new TestDto(1234, "Test Name");
        InnerResponseDto<TestDto> responseDto = InnerResponseDto.of(InnerResponseCodeType.OK, testDto);

        //when
        String json = null;
        try {
            json = objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //then
        System.out.println("===> json = " + json);
        assertThat(json).contains("code");
        assertThat(json).contains("0000");
        assertThat(json).contains("message");
        assertThat(json).contains("정상");
        assertThat(json).contains("result");
        assertThat(json).contains("SINGLE");
        assertThat(json).doesNotContain("errors");
    }

    @Test
    @DisplayName("Response 객체 - LIST Result - 0 Size")
    void listResultResponseZeroSize() {
        //given
        List<TestDto> testDtoList = List.of();
        InnerResponseDto<List<TestDto>> responseDto = InnerResponseDto.of(testDtoList);

        //when
        String json = null;
        try {
            json = objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //then
        System.out.println("===> json = " + json);
        assertThat(json).contains("code");
        assertThat(json).contains("0204");
        assertThat(json).contains("message");
        assertThat(json).contains("조회 데이터 없음");
        assertThat(json).contains("result");
        assertThat(json).contains("LIST");
        assertThat(json).doesNotContain("errors");
    }

    @Test
    @DisplayName("ResponseEntity 응답 - No Result")
    void basicResponseEntity() {
        //given
        ResponseEntity<InnerResponseDto<Object>> responseEntity = InnerResponseDto.toResponseEntity(InnerResponseCodeType.OK);

        //when
        String json = null;
        try {
            json = objectMapper.writeValueAsString(responseEntity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //then
        System.out.println("===> json = " + json);
        assertThat(json).contains("headers");
        assertThat(json).contains("body");
        assertThat(json).contains("statusCode");
        assertThat(json).contains("OK");
        assertThat(json).contains("statusCodeValue");
        assertThat(json).contains("200");

        assertThat(json).contains("code");
        assertThat(json).contains("0000");
        assertThat(json).contains("message");
        assertThat(json).contains("정상");
        assertThat(json).doesNotContain("result");
        assertThat(json).doesNotContain("errors");
    }

    @Test
    @DisplayName("ResponseEntity 응답 - SINGLE Result")
    void singleResultResponseEntity() {
        //given
        TestDto testDto = new TestDto(1234, "Test Name");
        ResponseEntity<InnerResponseDto<TestDto>> responseEntity = InnerResponseDto.toResponseEntity(testDto);

        //when
        String json = null;
        try {
            json = objectMapper.writeValueAsString(responseEntity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //then
        System.out.println("===> json = " + json);
        assertThat(json).contains("headers");
        assertThat(json).contains("body");
        assertThat(json).contains("statusCode");
        assertThat(json).contains("OK");
        assertThat(json).contains("statusCodeValue");
        assertThat(json).contains("200");

        assertThat(json).contains("code");
        assertThat(json).contains("0000");
        assertThat(json).contains("message");
        assertThat(json).contains("정상");
        assertThat(json).contains("result");
        assertThat(json).contains("SINGLE");
        assertThat(json).doesNotContain("errors");
    }

    @Test
    @DisplayName("ResponseEntity 객체 - LIST Result")
    void listResultResponseEntity() {
        //given
        TestDto testDto1 = new TestDto(1234, "Test Name 1");
        TestDto testDto2 = new TestDto(5678, "Test Name 2");
        List<TestDto> testDtoList = List.of(testDto1, testDto2);
        ResponseEntity<InnerResponseDto<List<TestDto>>> responseEntity
            = InnerResponseDto.toResponseEntity(InnerResponseCodeType.OK, testDtoList);

        //when
        String json = null;
        try {
            json = objectMapper.writeValueAsString(responseEntity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //then
        System.out.println("===> json = " + json);
        assertThat(json).isNotNull();

        assertThat(json).contains("headers");
        assertThat(json).contains("body");
        assertThat(json).contains("statusCode");
        assertThat(json).contains("OK");
        assertThat(json).contains("statusCodeValue");
        assertThat(json).contains("200");

        assertThat(json).contains("code");
        assertThat(json).contains("0000");
        assertThat(json).contains("message");
        assertThat(json).contains("정상");
        assertThat(json).contains("result");
        assertThat(json).contains("LIST");
        assertThat(json).doesNotContain("errors");
    }

    @Test
    @DisplayName("Response 객체 - LIST Result 매핑")
    void listResultResponseEntityMapping() {
        //given
        TestDto testDto1 = new TestDto(1234, "Test Name 1");
        TestDto testDto2 = new TestDto(5678, "Test Name 2");
        List<TestDto> testDtoList = List.of(testDto1, testDto2);
        InnerResponseDto<List<TestDto>> responseDto = InnerResponseDto.of(testDtoList);

        //when
        InnerResponseDto<List<TestDto>> responseDtoMapping = null;
        try {
            String json = objectMapper.writeValueAsString(responseDto);
            responseDtoMapping = objectMapper.readValue(json, new TypeReference<>() { });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //then
        System.out.println("===> responseDtoMapping = " + responseDtoMapping);
        assertThat(responseDtoMapping).isNotNull();

        assertThat(responseDtoMapping.getCode()).isEqualTo("0000");
        assertThat(responseDtoMapping.getMessage()).isEqualTo("정상");
        assertThat(responseDtoMapping.getResult()).isInstanceOf(InnerResponseResultDto.class);
        assertThat(responseDtoMapping.getResult().getData().size()).isEqualTo(testDtoList.size());
    }

    @Test
    @DisplayName("ResponseEntity 응답 - Parameter Error")
    void parameterErrorResponseEntity() {
        //given
        InnerResponseDto<Object> responseDto = InnerResponseDto.of(InnerResponseCodeType.BAD_REQUEST);
        responseDto.addResponseError(InnerResponseErrorDto.of(InnerResponseErrorType.PARAMETER_ERROR, "첫번째 파라미터가 잘못 되었어요."));
        ResponseEntity<InnerResponseDto<Object>> responseEntity = responseDto.toResponseEntity();

        //when
        String json = null;
        try {
            json = objectMapper.writeValueAsString(responseEntity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        boolean hasResponseError = responseDto.hasResponseError();

        //then
        System.out.println("===> json = " + json);
        assertThat(json).contains("headers");
        assertThat(json).contains("body");
        assertThat(json).contains("statusCode");
        assertThat(json).contains("BAD_REQUEST");
        assertThat(json).contains("statusCodeValue");
        assertThat(json).contains("400");

        assertThat(json).contains("code");
        assertThat(json).contains("0400");
        assertThat(json).contains("message");
        assertThat(json).contains("요청 파라미터 오류");
        assertThat(json).doesNotContain("result");
        assertThat(json).contains("errors");

        assertThat(hasResponseError).isTrue();
    }

    @Test
    @DisplayName("Response 객체 - Parameter Error 매핑")
    void parameterErrorResponseEntityMapping() {
        //given
        InnerResponseDto<Object> responseDto = InnerResponseDto.of(InnerResponseCodeType.BAD_REQUEST);
        responseDto.addResponseError(InnerResponseErrorDto.of(InnerResponseErrorType.PARAMETER_ERROR, "첫번째 파라미터가 잘못 되었어요."));
        responseDto.addResponseError(InnerResponseErrorDto.of(InnerResponseErrorType.PARAMETER_ERROR));

        //when
        InnerResponseDto<Object> responseDtoMapping = null;
        try {
            String json = objectMapper.writeValueAsString(responseDto);
            responseDtoMapping = objectMapper.readValue(json, new TypeReference<>() { });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //then
        System.out.println("===> responseDtoMapping = " + responseDtoMapping);
        assertThat(responseDtoMapping).isNotNull();

        assertThat(responseDtoMapping.getCode()).isEqualTo("0400");
        assertThat(responseDtoMapping.getMessage()).isEqualTo("요청 파라미터 오류");
        assertThat(responseDtoMapping.getResponseErrorSize()).isEqualTo(responseDto.getResponseErrorSize());
    }

    @Test
    @DisplayName("ResponseEntity 응답 - Server Error")
    void serverErrorResponseEntity() {
        //given
        InnerResponseDto<Object> responseDto = InnerResponseDto.of(InnerResponseCodeType.INTERNAL_SERVER_ERROR);
        InnerResponseErrorDto responseErrorDto1 = InnerResponseErrorDto.of(InnerResponseErrorType.INTERNAL_SERVER_ERROR, "서버에서 알 수 없는 오류가 발생했어요.");
        InnerResponseErrorDto responseErrorDto2 = InnerResponseErrorDto.of(InnerResponseErrorType.PARAMETER_ERROR, "파라미터가 잘못 되었어요.");
        List<InnerResponseErrorDto> responseErrors = List.of(responseErrorDto1, responseErrorDto2);
        responseDto.addResponseErrors(responseErrors);
        ResponseEntity<InnerResponseDto<Object>> responseEntity = responseDto.toResponseEntity();

        //when
        String json = null;
        try {
            json = objectMapper.writeValueAsString(responseEntity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //then
        System.out.println("===> json = " + json);
        assertThat(json).contains("headers");
        assertThat(json).contains("body");
        assertThat(json).contains("statusCode");
        assertThat(json).contains("INTERNAL_SERVER_ERROR");
        assertThat(json).contains("statusCodeValue");
        assertThat(json).contains("500");

        assertThat(json).contains("code");
        assertThat(json).contains("0500");
        assertThat(json).contains("message");
        assertThat(json).contains("내부 서버 오류");
        assertThat(json).doesNotContain("result");
        assertThat(json).contains("errors");
    }

    @Test
    @DisplayName("ResponseEntity 객체 - LIST Result 페이지")
    void listResultWithPageResponseEntity() {
        //given
        TestDto testDto1 = new TestDto(1234, "Test Name 1");
        TestDto testDto2 = new TestDto(5678, "Test Name 2");
        List<TestDto> testDtoList = List.of(testDto1, testDto2);
        InnerResponseDto<List<TestDto>> responseDto = InnerResponseDto.of(InnerResponseCodeType.OK, testDtoList);

        //when
        int page = 1;
        int rowSize = 30;
        int pageCount = 15;
        responseDto.getResult().setDataPage(page, rowSize, pageCount);
        ResponseEntity<InnerResponseDto<List<TestDto>>> responseEntity = responseDto.toResponseEntity();

        String json = null;
        try {
            json = objectMapper.writeValueAsString(responseEntity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //then
        System.out.println("===> json = " + json);
        assertThat(json).contains("headers");
        assertThat(json).contains("body");
        assertThat(json).contains("statusCode");
        assertThat(json).contains("OK");
        assertThat(json).contains("statusCodeValue");
        assertThat(json).contains("200");

        assertThat(json).contains("code");
        assertThat(json).contains("0000");
        assertThat(json).contains("message");
        assertThat(json).contains("정상");
        assertThat(json).contains("result");
        assertThat(json).contains("LIST");
        assertThat(json).doesNotContain("errors");

        assertThat(json).contains("dataPage");
        assertThat(json).contains("page");
        assertThat(json).contains("rowSize");
        assertThat(json).contains("pageCount");
    }

    @Test
    @DisplayName("ResponseEntity 객체 - LIST Result 페이지 매핑")
    void listResultWithPageResponseEntityMapping() {
        //given
        TestDto testDto1 = new TestDto(1234, "Test Name 1");
        TestDto testDto2 = new TestDto(5678, "Test Name 2");
        List<TestDto> testDtoList = List.of(testDto1, testDto2);
        InnerResponseDto<List<TestDto>> responseDto = InnerResponseDto.of(InnerResponseCodeType.OK, testDtoList);

        //when
        int page = 1;
        int rowSize = 30;
        int pageCount = 15;
        responseDto.getResult().setDataPage(page, rowSize, pageCount);
        System.out.println("===> responseDto = " + responseDto);

        InnerResponseDto<List<TestDto>> responseDtoMapping = null;
        try {
            String json = objectMapper.writeValueAsString(responseDto);
            responseDtoMapping = objectMapper.readValue(json, new TypeReference<>() { });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //then
        System.out.println("===> responseDtoMapping = " + responseDtoMapping);
        assertThat(responseDtoMapping).isNotNull();

        assertThat(responseDtoMapping.getCode()).isEqualTo("0000");
        assertThat(responseDtoMapping.getMessage()).isEqualTo("정상");
        assertThat(responseDtoMapping.getResult()).isInstanceOf(InnerResponseResultDto.class);
        assertThat(responseDtoMapping.getResult().getData().size()).isEqualTo(testDtoList.size());
    }
}