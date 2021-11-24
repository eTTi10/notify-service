package com.lguplus.fleta.provider.rest.push;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Push G/W 요청에 대한 결과 클래스 
 *
 */

@Getter
@Setter
@NoArgsConstructor
public class PushResultVO extends BaseVO{

	private String flag;	//결과 코드
	private String message;	//결과 메시지
	private String statusCode; // return 결과코드

}
