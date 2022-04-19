package com.lguplus.fleta.data.type.converter;

import com.lguplus.fleta.data.type.NetworkInfo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Minwoo Lee
 * @since 1.0
 */
@Component
public class NetworkInfoConverter implements Converter<String, NetworkInfo> {

	/**
	 * 
	 */
	@Override
	public NetworkInfo convert(final String source) {

		return NetworkInfo.asValue(source);
	}
}
