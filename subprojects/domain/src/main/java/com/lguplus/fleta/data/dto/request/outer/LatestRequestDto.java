package com.lguplus.fleta.data.dto.request.outer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lguplus.fleta.data.dto.PlainTextibleDto;
import com.lguplus.fleta.data.dto.response.CommonResponseDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
public class LatestRequestDto implements PlainTextibleDto, Serializable {

    private static final long serialVersionUID = 1L;//무엇????

    @JsonProperty("sa_id")  //가입자 번호
    private String saId;

    @JsonProperty("mac") //가입자 맥주소
    private String mac;

    @JsonProperty("ctn") //전화번호 or 단말 맥 어드레스
    private String ctn;

    @JsonProperty("cat_id") //카테고리 아이디
    private String catId;

    @Override
    public String toPlainText() {
        return String.join(CommonResponseDto.Separator.COLUMN
                , getSaId(), getMac(), getCtn());
    }
}
