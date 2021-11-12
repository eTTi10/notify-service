package com.lguplus.fleta.provider.rest.push;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PushBaseVo {

	private String app_id;
	private String service_id;
	private String service_key;
	private String noti_message;
	private List<String> arrItem;

}
