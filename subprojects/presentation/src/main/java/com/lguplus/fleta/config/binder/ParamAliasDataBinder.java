package com.lguplus.fleta.config.binder;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParamAliasDataBinder extends ExtendedServletRequestDataBinder {

	/**
	 * 
	 */
	private final Map<String, String> paramAliases;

	/**
	 *
	 * @param target
	 * @param objectName
	 * @param paramAliases
	 */
	public ParamAliasDataBinder(final Object target, final String objectName,
								final Map<String, String> paramAliases) {

		super(target, objectName);

		this.paramAliases = paramAliases;
	}

	/**
	 * 
	 */
	@Override
	protected void addBindValues(final MutablePropertyValues propertyValues,
			final ServletRequest request) {

		super.addBindValues(propertyValues, request);

		for (final Entry<String, String> entry : paramAliases.entrySet()) {
			final String paramAlias = entry.getKey();
			final PropertyValue propertyValue = propertyValues.getPropertyValue(paramAlias);
			if (propertyValue != null) {
				propertyValues.add(entry.getValue(), propertyValue.getValue());
			}
		}
	}
}
