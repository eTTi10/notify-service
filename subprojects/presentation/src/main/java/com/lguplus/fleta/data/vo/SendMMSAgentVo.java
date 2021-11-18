package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.validation.Groups;
import lombok.Getter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, LatestSearchRequestVo.class})
public class SendMMSAgentVo {
    @ParamAlias("sa_id")
    @NotBlank(message = "[가입자 번호]가 입력되지 않았습니다.", groups = Groups.C1.class)
    private String saId;

    @ParamAlias("stb_mac")
    @NotBlank(message = "[가입자 맥주소]가 입력되지 않았습니다.", groups = Groups.C2.class)
    private String stbMac;

    @ParamAlias("mms_cd")
    @NotBlank(message = "[MMS코드]가 입력되지 않았습니다.", groups = Groups.C3.class)
    private String mmsCd;

    @ParamAlias("ctn")
    @NotBlank(message = "[전화번호 or 단말 맥 어드레스]가 입력되지 않았습니다.", groups = Groups.C4.class)
    private String ctn;

    @ParamAlias("replacement")
    private String replacement;

    public SendMMSRequestDto convert() {
        return SendMMSRequestDto.builder()
                .saId(this.getSaId())
                .stbMac(this.getStbMac())
                .mmsCd(this.getMmsCd())
                .ctn(this.getCtn())
                .replacement(this.getReplacement())
                .build();
    }
}