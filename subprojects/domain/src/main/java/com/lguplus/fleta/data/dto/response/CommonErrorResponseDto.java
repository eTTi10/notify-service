package com.lguplus.fleta.data.dto.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Minwoo Lee
 * @since 1.0
 */
@JacksonXmlRootElement(localName = "error")
public interface CommonErrorResponseDto extends CommonResponseDto {

    @JsonGetter("code")
    String getFlag();

}
