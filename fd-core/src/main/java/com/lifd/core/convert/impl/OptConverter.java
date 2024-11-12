package com.lifd.core.convert.impl;

import com.lifd.core.convert.AbstractConverter;
import com.lifd.core.lang.Opt;

/**
 *
 * {@link Opt}对象转换器
 *
 * *
 */
public class OptConverter extends AbstractConverter<Opt<?>> {
	private static final long serialVersionUID = 1L;

	@Override
	protected Opt<?> convertInternal(Object value) {
		return Opt.ofNullable(value);
	}

}
