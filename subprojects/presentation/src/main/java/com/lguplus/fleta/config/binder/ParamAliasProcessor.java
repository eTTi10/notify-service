package com.lguplus.fleta.config.binder;

import com.lguplus.fleta.data.annotation.ParamAlias;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Minwoo Lee
 * @since 1.0
 */
public class ParamAliasProcessor extends ServletModelAttributeMethodProcessor {

	/**
	 * 
	 */
	private static final Map<Class<?>, Map<String, String>> CACHE = new ConcurrentHashMap<>();

	/**
	 * 
	 */
	public ParamAliasProcessor() {

		super(false);
	}

	/**
	 *
	 */
	@Override
	public boolean supportsParameter(final MethodParameter parameter) {

		return !getParamAliases(parameter.getParameterType()).isEmpty();
	}

	/**
	 * 
	 */
	@Override
	protected void bindRequestParameters(final WebDataBinder binder,
			final NativeWebRequest request) {

		final Object target = binder.getTarget();
		if (target == null) {
			return;
		}

		final ParamAliasDataBinder newBinder = new ParamAliasDataBinder(target,
				binder.getObjectName(), getParamAliases(target.getClass()));
		newBinder.initDirectFieldAccess();
		newBinder.setConversionService(binder.getConversionService());
		final List<Validator> validators = binder.getValidators();
		newBinder.addValidators(validators.toArray(new Validator[validators.size()]));

		super.bindRequestParameters(newBinder, request);

		binder.getBindingResult().addAllErrors(newBinder.getBindingResult());
	}

	/**
	 * 
	 * @param targetClass
	 * @return
	 */
	private Map<String, String> getParamAliases(final Class<?> targetClass) {

		if (CACHE.containsKey(targetClass)) {
			return CACHE.get(targetClass);
		}

		final Map<String, String> paramAliases = new HashMap<>();
		Class<?> aClass = targetClass;
		do {
			for (final Field field : aClass.getDeclaredFields()) {
				final ParamAlias annotation = field.getAnnotation(ParamAlias.class);
				if (annotation == null) {
					continue;
				}

				Arrays.stream(annotation.value())
						.filter(StringUtils::isNotBlank)
						.forEach(value -> paramAliases.put(value, field.getName()));
			}

			aClass = aClass.getSuperclass();
		} while (aClass != null);
  
		CACHE.put(targetClass, paramAliases);
		return paramAliases;
	}
}
