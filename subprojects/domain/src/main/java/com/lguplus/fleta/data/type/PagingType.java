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
public enum PagingType {

	/**
	 *
	 */
	NEW("N"),

	/**
	 *
	 */
	UNDEFINED("");

	/**
	 *
	 */
	private static final Map<String, PagingType> all = Stream.of(values())
			.collect(Collectors.toMap(PagingType::toString, Function.identity()));

	/**
	 *
	 */
	private final String code;

	/**
	 *
	 */
	PagingType(final String code) {

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
	public static PagingType asValue(final String code) {

		if (code == null || code.isBlank()) {
			return null;
		}

		final PagingType pagingType = all.get(code);
		if (pagingType == null) {
			return UNDEFINED;
		} else {
			return pagingType;
		}
	}
}
