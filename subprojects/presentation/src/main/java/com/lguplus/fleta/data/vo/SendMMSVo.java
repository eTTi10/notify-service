package com.lguplus.fleta.data.vo;

import com.lguplus.fleta.data.annotation.ParamAlias;
import com.lguplus.fleta.data.dto.request.SendMMSRequestDto;
import com.lguplus.fleta.exception.ParameterContainsWhitespaceException;
import com.lguplus.fleta.exception.ParameterTypeMismatchException;
import com.lguplus.fleta.validation.Groups;
import com.lguplus.fleta.validation.NetworkTypePattern;
import lombok.Getter;
import lombok.ToString;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@ToString
@GroupSequence({Groups.C1.class, Groups.C2.class, Groups.C3.class, Groups.C4.class, Groups.C5.class, Groups.C6.class, LatestSearchRequestVo.class})
public class SendMMSVo {
    @ParamAlias("sa_id")
    @NotBlank(message = "sa_id 값에 공백이 없어야 합니다.", groups = Groups.C1.class)//5008
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "sa_id의 패턴이 일치하지 않습니다.", payload = ParameterTypeMismatchException.class, groups = Groups.C5.class)//5008
    private String saId;

    @ParamAlias("stb_mac")
    @NotBlank(message = "stb_mac 값에 공백이 없어야 합니다.", groups = Groups.C2.class)//5008
    @Pattern(regexp = "^[a-zA-Z0-9.]*$", message = "stb_mac의 패턴이 일치하지 않습니다.", payload = ParameterTypeMismatchException.class, groups = Groups.C6.class)//5008
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

/*

    private void validationCheckQual(String sa_id, String stb_mac, String mms_cd, String ctn) throws Exception{

		//특문
		Pattern stbMacPattern = Pattern.compile("^[a-zA-Z0-9.]*$");
		Pattern pSpecialCharacter = Pattern.compile("^[A-Za-z0-9]*$");
		Matcher mSpecialCharacter = pSpecialCharacter.matcher(sa_id);

		Matcher mSpecialCharacter2 = stbMacPattern.matcher(stb_mac);
		if(!mSpecialCharacter2.find()){
			throw new CustomExceptionHandler("5008","stb_mac의 패턴이 일치하지 않습니다.");
		}
		Matcher mSpecialCharacter3 = pSpecialCharacter.matcher(ctn);
		if(!mSpecialCharacter3.find()){
			throw new CustomExceptionHandler("5008","ctn의 패턴이 일치하지 않습니다.");
		}
		Matcher mSpecialCharacter4 = pSpecialCharacter.matcher(mms_cd);
		if(!mSpecialCharacter4.find()){
			throw new CustomExceptionHandler("5008","mms_cd의 패턴이 일치하지 않습니다.");
		}

		//공백
		Pattern pBlank = Pattern.compile("\\s");
		Matcher mBlank = pBlank.matcher(sa_id);
		if(mBlank.find()) {
			throw new CustomExceptionHandler("5008","sa_id 값에 공백이 없어야 합니다.");
		}
		Matcher mBlank1 = pBlank.matcher(stb_mac);
		if(mBlank1.find()) {
			throw new CustomExceptionHandler("5008","stb_mac 값에 공백이 없어야 합니다.");
		}
		Matcher mBlank2 = pBlank.matcher(ctn);
		if(mBlank2.find()) {
			throw new CustomExceptionHandler("5008","ctn 값에 공백이 없어야 합니다.");
		}
		Matcher mBlank3 = pBlank.matcher(mms_cd);
		if(mBlank3.find()) {
			throw new CustomExceptionHandler("5008","mms_cd 값에 공백이 없어야 합니다.");
		}

		//길이(최소)
		if(sa_id.length() < 8){
			throw new CustomExceptionHandler("5002","sa_id 의 길이가 8보다 작습니다.");
		}
		if(stb_mac.length() < 10){
			throw new CustomExceptionHandler("5002","stb_mac 의 길이가 10보다 작습니다.");
		}
		//길이(최대)
		if(sa_id.length() > 15){
			throw new CustomExceptionHandler("5002","sa_id 의 길이가 10보다 큽니다.");
		}
		if(stb_mac.length() > 20){
			throw new CustomExceptionHandler("5002","stb_mac 의 길이가 20보다 큽니다.");
		}

		//ctn
		Pattern pattern = Pattern.compile("^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})$");
    	Matcher matcher = pattern.matcher(ctn);
    	if(!matcher.matches()){
            throw new CustomExceptionHandler(Properties.getProperty("flag.phone_number_error"), Properties.getProperty("message.phone_number_error"));
    	}
	}




*/



}