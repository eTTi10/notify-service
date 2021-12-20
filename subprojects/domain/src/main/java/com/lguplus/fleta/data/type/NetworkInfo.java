package com.lguplus.fleta.data.type;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public enum NetworkInfo {

	/**
	 *
	 */
	_3G("3G"),

	/**
	 *
	 */
	_4G("4G"),

	/**
	 *
	 */
	WIFI("WIFI"),

	/**
	 *
	 */
	WIRE("WIRE"),

	/**
	 *
	 */
	ETC("ETC"),

	/**
	 *
	 */
	UNDEFINED("");

	/**
	 *
	 */
	private static final Map<String, NetworkInfo> all = Stream.of(values())
			.collect(Collectors.toMap(NetworkInfo::toString, Function.identity()));

	/**
	 *
	 */
	private final String code;

	/**
	 *
	 */
	NetworkInfo(final String code) {

		this.code = code;
	}

	/**
	 *
	 */
	@Override
	public String toString() {

		return code;
	}

	/**
	 *
	 * @param code
	 * @return
	 */
	public static NetworkInfo asValue(final String code) {

		if (code == null || code.isBlank()) {
			return null;
		}

		final NetworkInfo networkInfo = all.get(code);
		if (networkInfo == null) {
			return UNDEFINED;
		} else {
			return networkInfo;
		}
	}
}
