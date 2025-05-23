package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.confirm;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.confirm.ConfirmCodeDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

@Component
public class ConfirmCodeValidator implements Validator {

	@Value("#{T(java.util.regex.Pattern).compile('${validator.pattern.email}')}")
	private Pattern EMAIL_PATTERN;

	@Override
	public boolean supports(Class<?> clazz) {
		return ConfirmCodeDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ConfirmCodeDto dto = (ConfirmCodeDto) target;
		validateEmail(dto.email(), errors);
		validateCode(dto.code(), errors);
	}

	/**
	 * EMAIL 필드 값의 공통 검증(비어있는지, 공백 포함, 패턴)을 수행합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	private void validateEmail(String email, Errors errors) {
		if (rejectIfEmailEmpty(email, errors)) {
			return;
		}
		if (rejectIfEmailWhitespace(email, errors)) {
			return;
		}
		rejectIfEmailPattern(email, errors);
	}


	/**
	 * CODE 필드 검증(비어있는지, 공백 포함, 숫자 6자리 여부)을 수행합니다.
	 *
	 * @param code   검증할 인증 코드 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	private void validateCode(String code, Errors errors) {
		if (rejectIfCodeEmpty(code, errors)) {
			return;
		}
		rejectIfCodeInvalidFormat(code, errors);
	}


	/**
	 * EMAIL 값이 비어있는지 확인하고, 비어있으면 오류 등록 후 true를 반환합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private boolean rejectIfEmailEmpty(String email, Errors errors) {
		if (!StringUtils.hasText(email)) {
			AuthValidationError.EMAIL_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * EMAIL 값에 공백이 포함되어 있는지 확인하고, 포함 시 오류 등록 후 true를 반환합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private boolean rejectIfEmailWhitespace(String email, Errors errors) {
		if (StringUtils.containsWhitespace(email)) {
			AuthValidationError.EMAIL_WHITESPACE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * EMAIL 패턴이 올바른지 확인하고, 올바르지 않으면 오류를 등록합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private void rejectIfEmailPattern(String email, Errors errors) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			AuthValidationError.EMAIL_PATTERN.reject(errors);
		}
	}

	/**
	 * CODE 값이 비어있는지 확인하고, 비어있으면 오류 등록 후 true를 반환합니다.
	 *
	 * @param code   검증할 인증 코드 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	private boolean rejectIfCodeEmpty(String code, Errors errors) {
		if (!StringUtils.hasText(code)) {
			AuthValidationError.CODE_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * CODE 값이 숫자 6자리인지 확인하고, 아니면 오류를 등록합니다.
	 *
	 * @param code   검증할 인증 코드 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	private void rejectIfCodeInvalidFormat(String code, Errors errors) {
		if (!code.matches("\\d{6}")) {
			AuthValidationError.CODE_INVALID_FORMAT.reject(errors);
		}
	}
}
