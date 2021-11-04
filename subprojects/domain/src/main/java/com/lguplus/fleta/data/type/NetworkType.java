package com.lguplus.fleta.data.type;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Minwoo Lee
 * @since 1.0
 */
public enum NetworkType {

	/**
	 *
	 */
	OPTICAL_LAN("02"),

	/**
	 *
	 */
	HFC_100M("01"),

	/**
	 *
	 */
	HFC_10M("31"),

	/**
	 *
	 */
	UNDEFINED("");

	/**
	 *
	 */
	private static final Map<String, NetworkType> all = Stream.of(values())
			.collect(Collectors.toMap(NetworkType::toString, Function.identity()));

	/**
	 *
	 */
	private final String code;

	/**
	 *
	 */
    NetworkType(final String code) {

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
	public static NetworkType asValue(final String code) {

		if (code == null || code.isBlank()) {
			return null;
		}

		final NetworkType networkType = all.get(code);
		if (networkType == null) {
			return fromNumber(code);
		} else {
			return networkType;
		}
	}

	private static NetworkType fromNumber(final String code) {

		try {
			final String newCode = StringUtils.leftPad(String.valueOf(Integer.parseInt(code)), 2, "0");
			final NetworkType networkType = all.get(newCode);
			if (networkType != null) {
				return networkType;
			}
		} catch (final NumberFormatException e) {
			// Do nothing.
		}
		return UNDEFINED;
	}
}
