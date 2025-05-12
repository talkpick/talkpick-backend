package com.likelion.backendplus4.talkpick.backend.common.configuration.valid;

import java.util.List;

import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalValidationAdvice {

	private final LocalValidatorFactoryBean jsrValidator;
	private final List<Validator> customValidators;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(jsrValidator);
		Class<?> target = binder.getTarget() != null
			? binder.getTarget().getClass()
			: null;
		if (target == null) return;

		// supports()가 true인 Validator만 등록
		customValidators.stream()
			.filter(v -> v.supports(target))
			.forEach(binder::addValidators);
	}
}
