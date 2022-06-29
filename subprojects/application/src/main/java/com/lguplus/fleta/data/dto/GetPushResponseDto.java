package com.lguplus.fleta.data.dto;

import com.lguplus.fleta.data.dto.response.CommonResponseDto;
//import javax.xml.bind.annotation.XmlElement;
import com.lguplus.fleta.data.dto.response.outer.GetPushDto;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author Minwoo Lee
 * @since 1.0
 */
@SuperBuilder
public class GetPushResponseDto implements CommonResponseDto {

	public static final String COLSEP = "!^"; // 열 분리자 -> constants로 변경 필요

	private static final String SUCCESS_FLAG = "0000";

	private static final String SUCCESS_MESSAGE = "성공";

	@Override
	public String getFlag() {
		return SUCCESS_FLAG;
	}

	@Override
	public String getMessage() {
		return SUCCESS_MESSAGE;
	}

	/**
	 * 푸시등록여부
	 * Y:알림등록
	 * N:알림해제
	 */
	private String push_yn = "";

	/**
	 * 공연시작일시
	 * (yyyyMMddHHmm)
	 */
	private String start_dt = "";

	/**
	 * Y:알림등록
	 * N:알림해제
	 * @return 푸시등록여부
	 */
//	@XmlElement(name="push_yn")
	public String getPush_yn() {
		return push_yn;
	}

	/**
	 * Y:알림등록
	 * N:알림해제
	 * @param push_yn 푸시등록여부
	 */
	public void setPush_yn(String push_yn) {
		this.push_yn = push_yn;
	}

	/**
	 * 공연시작일시
	 * (yyyyMMddHHmm)
	 * @return
	 */
//	@XmlElement(name="start_dt")
	public String getStart_dt() {
		return checkNullStr(start_dt);
	}

	/**
	 * 공연시작일시
	 * (yyyyMMddHHmm)
	 * @param start_dt
	 */
	public void setStart_dt(String start_dt) {
		this.start_dt = start_dt;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(COLSEP);
		sb.append(push_yn);
		sb.append(COLSEP);
		sb.append(start_dt);

		return sb.toString();
	}

	public String checkNullStr(String str) { // TODO: 2022/06/23 util로 만들기
		if ((str == null) || (str.trim().equals("")) || (str.trim().equalsIgnoreCase("null")) || (str.trim().length() == 0) || (str.equalsIgnoreCase("undefined")))
			return "";
		else
			return str.trim();
	}


	public static GetPushResponseDto create(GetPushDto dto) {
		return GetPushResponseDto.builder()
			.push_yn(dto.getPushYn())
			.start_dt(dto.getStartDt())
			.build();
	}
}
