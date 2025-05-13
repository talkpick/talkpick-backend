package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.RefreshReqDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

@Component
public class RefreshReqValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return RefreshReqDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RefreshReqDto dto = (RefreshReqDto) target;
		if (!StringUtils.hasText(dto.refreshToken())) {
			AuthValidationError.REFRESH_TOKEN_EMPTY.reject(errors);
		}
	}
}