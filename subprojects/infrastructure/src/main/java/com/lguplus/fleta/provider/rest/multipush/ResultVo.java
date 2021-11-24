package com.lguplus.fleta.provider.rest.multipush;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ResultVo implements Serializable{
	private String flag = "";
	private String message = "";
}
