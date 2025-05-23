package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import static org.springframework.util.StringUtils.*;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

/**
 * 공통 계정·비밀번호 검증 로직을 제공하는 추상 Validator.
 *
 * @since 2025-05-12
 * @modified 2025-05-14
 */
public abstract class AbstractAuthValidator<T> implements Validator {
	private final Class<T> targetType;

	@Value("#{T(java.util.regex.Pattern).compile('${validator.pattern.password}')}")
	private Pattern PASSWORD_PATTERN;

	protected AbstractAuthValidator(Class<T> targetType) {
		this.targetType = targetType;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return targetType.isAssignableFrom(clazz);
	}

	@Override
	public final void validate(Object target, Errors errors) {
		T dto = targetType.cast(target);
		validateAccount(extractAccount(dto), errors);
		validatePasswordWithPattern(extractPassword(dto), errors);
		validateAdditional(dto, errors);
	}

	/** 서브클래스가 DTO에서 account 값을 꺼내올 메서드 */
	protected abstract String extractAccount(T dto);

	/** 서브클래스가 DTO에서 password 값을 꺼내올 메서드 */
	protected abstract String extractPassword(T dto);

	/** 서브클래스가 추가 필드를 검증할 때 오버라이드 */
	protected void validateAdditional(T dto, Errors errors) {
	}

	/**
	 * account 필드를 검증합니다.
	 *  - null/blank
	 *  - 공백 포함 금지
	 *  - 길이 4~20자
	 *
	 * @param account 계정 문자열
	 * @param errors  Errors
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-14
	 */
	private void validateAccount(String account, Errors errors) {
		if (isAccountEmpty(account, errors)) {
			return;
		}
		if (hasAccountWhitespace(account, errors)) {
			return;
		}
		checkAccountSize(account, errors);
	}

	/**
	 * password 필드를 종합 검증합니다.
	 *  - null/blank
	 *  - 공백 포함 금지
	 *  - 길이 8~16자
	 *  - 패턴(대문자·소문자·숫자·특수문자 각각 1회 이상)
	 *
	 * @param password 비밀번호 문자열
	 * @param errors   Errors
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-14
	 */
	private void validatePasswordWithPattern(String password, Errors errors) {
		if (isPasswordEmpty(password, errors)) {
			return;
		}
		if (hasPasswordWhitespace(password, errors)) {
			return;
		}
		if (isPasswordSizeInvalid(password, errors)) {
			return;
		}
		checkPasswordPattern(password, errors);
	}

	/**
	 * account가 null 또는 blank인지 확인하고, 빈 값일 경우 오류를 등록합니다.
	 *
	 * @param account 계정 문자열
	 * @param errors  Errors
	 * @return 빈 값일 경우 true, 아닐 경우 false
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 * @author 박찬병
	 */
	private boolean isAccountEmpty(String account, Errors errors) {
		if (!hasText(account)) {
			AuthValidationError.ACCOUNT_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * account에 공백이 포함되었는지 확인하고, 포함된 경우 오류를 등록합니다.
	 *
	 * @param account 계정 문자열
	 * @param errors  Errors
	 * @return 공백 포함 시 true, 아닐 시 false
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 * @author 박찬병
	 */
	private boolean hasAccountWhitespace(String account, Errors errors) {
		if (containsWhitespace(account)) {
			AuthValidationError.ACCOUNT_WHITESPACE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * account 길이가 4~20자 범위를 벗어났는지 확인하고, 벗어난 경우 오류를 등록합니다.
	 *
	 * @param account 계정 문자열
	 * @param errors  Errors
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 * @author 박찬병
	 */
	private void checkAccountSize(String account, Errors errors) {
		int length = account.length();
		if (4 > length || length > 20) {
			AuthValidationError.ACCOUNT_SIZE.reject(errors);
		}
	}

	/**
	 * 비밀번호가 null 또는 blank인지 확인하고, 빈 값일 경우 오류를 등록합니다.
	 *
	 * @param password 비밀번호 문자열
	 * @param errors   Errors
	 * @return 빈 값일 경우 true, 아닐 경우 false
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 * @author 박찬병
	 */
	private boolean isPasswordEmpty(String password, Errors errors) {
		if (!hasText(password)) {
			AuthValidationError.PASSWORD_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 비밀번호에 공백이 포함되었는지 확인하고, 포함된 경우 오류를 등록합니다.
	 *
	 * @param password 비밀번호 문자열
	 * @param errors   Errors
	 * @return 공백 포함 시 true, 아닐 시 false
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 * @author 박찬병
	 */
	private boolean hasPasswordWhitespace(String password, Errors errors) {
		if (containsWhitespace(password)) {
			AuthValidationError.PASSWORD_WHITESPACE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 비밀번호 길이가 8~20자 범위를 벗어났는지 확인하고, 벗어난 경우 오류를 등록합니다.
	 *
	 * @param password 비밀번호 문자열
	 * @param errors   Errors
	 * @return 크기 범위 벗어날 시 true, 아닐 시 false
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 * @author 박찬병
	 */
	private boolean isPasswordSizeInvalid(String password, Errors errors) {
		int length = password.length();
		if (7 >= length || length >= 21) {
			AuthValidationError.PASSWORD_SIZE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 비밀번호 패턴(대문자·소문자·숫자·특수문자 각 1회 이상) 매칭을 수행하고, 불일치 시 오류를 등록합니다.
	 *
	 * @param password 비밀번호 문자열
	 * @param errors   Errors
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 * @author 박찬병
	 */
	private void checkPasswordPattern(String password, Errors errors) {
		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			AuthValidationError.PASSWORD_PATTERN.reject(errors);
		}
	}
}