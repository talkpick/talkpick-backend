package com.likelion.backendplus4.talkpick.backend.common.configuration.valid;

import java.util.List;

import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.RequiredArgsConstructor;


/**
 * 모든 Validator(JSR-303 및 커스텀)를 글로벌 범위에서 WebDataBinder에 등록하는 클래스입니다.
 *
 * @since 2025-05-13
 * @modified 2025-05-13
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalValidationAdvice {

	private final LocalValidatorFactoryBean jsrValidator;
	private final List<Validator> customValidators;

    /**
     * WebDataBinder에 JSR-303 및 커스텀 Validator를 등록합니다.
     *
     * @param binder WebDataBinder
     * @author 박찬병
     * @since 2025-05-13
     * @modified 2025-05-13
     */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(jsrValidator);
		Class<?> target = binder.getTarget() != null
			? binder.getTarget().getClass()
			: null;
		if (target == null)
			return;

		customValidators.stream()
			.filter(v -> v.supports(target))
			.forEach(binder::addValidators);
	}
}
