package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.check.CheckAccountDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

@Component
public class CheckAccountValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return CheckAccountDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CheckAccountDto dto = (CheckAccountDto) target;
		validateAccount(dto.account(), errors);
	}

	/**
	 * ACCOUNT 필드 값의 공통 검증(비어있는지, 공백 포함, 길이)을 수행합니다.
	 *
	 * @param account 검증할 계정 문자열
	 * @param errors  오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private void validateAccount(String account, Errors errors) {
		if (rejectIfAccountEmpty(account, errors)) {
			return;
		}
		if (rejectIfAccountWhitespace(account, errors)) {
			return;
		}
		rejectIfAccountSize(account, errors);
	}

	/**
	 * ACCOUNT 값이 비어있는지 확인하고, 비어있으면 오류 등록 후 true를 반환합니다.
	 *
	 * @param account 검증할 계정 문자열
	 * @param errors  오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private boolean rejectIfAccountEmpty(String account, Errors errors) {
		if (!StringUtils.hasText(account)) {
			AuthValidationError.ACCOUNT_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * ACCOUNT 값에 공백이 포함되어 있는지 확인하고, 포함 시 오류 등록 후 true를 반환합니다.
	 *
	 * @param account 검증할 계정 문자열
	 * @param errors  오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private boolean rejectIfAccountWhitespace(String account, Errors errors) {
		if (StringUtils.containsWhitespace(account)) {
			AuthValidationError.ACCOUNT_WHITESPACE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * ACCOUNT 값의 길이가 4미만 또는 20초과인지 확인하여
	 * 조건을 만족하지 않으면 오류를 등록합니다.
	 *
	 * @param account 검증할 계정 문자열
	 * @param errors  오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private void rejectIfAccountSize(String account, Errors errors) {
		int len = account.length();
		if (4 > len || len > 20) {
			AuthValidationError.ACCOUNT_SIZE.reject(errors);
		}
	}

}

