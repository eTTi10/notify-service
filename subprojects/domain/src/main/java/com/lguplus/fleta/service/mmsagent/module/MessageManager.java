package com.lguplus.fleta.service.mmsagent.module;

import org.springframework.util.StringUtils;

import java.net.URLDecoder;

public class MessageManager {
	private final static String sep = "\\|";

	/**
	 * 지정된 문자열로 변경하여 리턴한다.
	 * @param msg
	 * @param replacement
	 * @return
	 */
	public static String convertMsg(String msg, String replacement){
		if(StringUtils.hasLength(replacement) 
				&& StringUtils.hasLength(msg)) {
			try {
				replacement = URLDecoder.decode(replacement, "UTF-8");
				
				String[] rep = replacement.split(sep);
				int i = 1;
				for(String t : rep){
					String repTxt = "{" + i + "}";
					msg = msg.replace(repTxt, t);
					i++;
				}
				return msg;
			} catch (Exception e) {
				return msg;
			}
		}else  return msg;
	}
}
