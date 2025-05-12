package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import java.util.regex.Pattern;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

/**
 * 공통 계정·비밀번호 검증 로직을 제공하는 추상 Validator.
 *
 * @param <T> 검증 대상 DTO 타입
 * @author 박찬병
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public abstract class AbstractAuthValidator<T> implements Validator {
	private final Class<T> targetType;

	/** 비밀번호 패턴: 8~16자, 대문자·소문자·숫자·특수문자 각각 최소 1회 포함 */
	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$"
	);

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
	protected void validateAdditional(T dto, Errors errors) { }

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
	 * @modified 2025-05-12
	 */
	private void validateAccount(String account, Errors errors) {
		if (account == null || account.isBlank()) {
			AuthValidationError.ACCOUNT_EMPTY.reject(errors);
			return;
		}
		if (account.contains(" ")) {
			AuthValidationError.ACCOUNT_WHITESPACE.reject(errors);
			return;
		}
		if (account.length() < 4 || account.length() > 20) {
			AuthValidationError.ACCOUNT_SIZE.reject(errors);
		}
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
	 * @modified 2025-05-12
	 */
	private void validatePasswordWithPattern(String password, Errors errors) {
		if (password == null || password.isBlank()) {
			AuthValidationError.PASSWORD_EMPTY.reject(errors);
			return;
		}
		if (password.contains(" ")) {
			AuthValidationError.PASSWORD_WHITESPACE.reject(errors);
			return;
		}
		if (password.length() < 8 || password.length() > 16) {
			AuthValidationError.PASSWORD_SIZE.reject(errors);
			return;
		}
		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			AuthValidationError.PASSWORD_PATTERN.reject(errors);
		}
	}
}