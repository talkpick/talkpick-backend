package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.LogoutReqDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

@Component
public class LogoutReqValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return LogoutReqDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		LogoutReqDto dto = (LogoutReqDto)target;
		if (dto.accessToken() == null || dto.accessToken().isBlank()) {
			AuthValidationError.ACCESS_TOKEN_EMPTY.reject(errors);
		}
	}
}