package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.recovery;

import static org.springframework.util.StringUtils.*;
import static org.springframework.util.StringUtils.containsWhitespace;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.recovery.RecoveryUserInfoDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

/**
 * 계정 복구 요청 시 사용자의 이메일, 이름, 계정 정보를 검증하는 Validator입니다.
 *
 * 이메일, 이름, 계정 각각의 필드에 대해 비어있음, 공백 포함 여부, 길이, 패턴 등 유효성을 검사합니다.
 *
 * @since 2025-05-21
 */
@Component
public class RecoveryUserInfoValidator implements Validator {

	@Value("#{T(java.util.regex.Pattern).compile('${validator.pattern.email}')}")
	private Pattern EMAIL_PATTERN;

	@Value("#{T(java.util.regex.Pattern).compile('${validator.pattern.name}')}")
	private Pattern NAME_PATTERN;


	@Override
	public boolean supports(Class<?> clazz) {
		return RecoveryUserInfoDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RecoveryUserInfoDto dto = (RecoveryUserInfoDto)target;
		validateEmail(dto.email(), errors);
		validateName(dto.name(), errors);
		validateAccount(dto.account(), errors);
	}

	/**
	 * EMAIL 필드 값의 공통 검증(비어있는지, 공백 포함, 패턴)을 수행합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-21
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
	 * 이름 값을 검증합니다.
	 *  - null 또는 빈 문자열 불가
	 *  - 공백 문자 불가
	 *  - 최대 30자
	 *  - 영문 또는 한글만 가능
	 *
	 * @param name 사용자의 이름
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-21
	 */
	private void validateName(String name, Errors errors) {
		if (isNameEmpty(name, errors))
			return;
		if (hasNameWhitespace(name, errors))
			return;
		checkNameSize(name, errors);
		checkNamePattern(name, errors);
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
	 * @since 2025-05-21
	 */
	private void validateAccount(String account, Errors errors) {
		if (account == null) {
			return;
		}
		if (isAccountEmpty(account, errors)) {
			return;
		}
		if (hasAccountWhitespace(account, errors)) {
			return;
		}
		checkAccountSize(account, errors);
	}


	/**
	 * EMAIL 값이 비어있는지 확인하고, 비어있으면 오류 등록 후 true를 반환합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-21
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
	 * @since 2025-05-21
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
	 * @since 2025-05-21
	 */
	private void rejectIfEmailPattern(String email, Errors errors) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			AuthValidationError.EMAIL_PATTERN.reject(errors);
		}
	}

	/**
	 * 이름이 null 또는 blank인지 확인하고, 빈 값일 경우 오류를 등록합니다.
	 *
	 * @param name   검증할 이름 문자열
	 * @param errors Errors 객체
	 * @return 빈 값일 경우 true, 아닐 경우 false
	 * @author 박찬병
	 * @since 2025-05-21
	 */
	private boolean isNameEmpty(String name, Errors errors) {
		if (!StringUtils.hasText(name)) {
			AuthValidationError.NAME_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 이름에 공백이 포함되었는지 확인하고, 포함된 경우 오류를 등록합니다.
	 *
	 * @param name   검증할 이름 문자열
	 * @param errors Errors 객체
	 * @return 공백이 포함된 경우 true, 아니면 false
	 * @author 박찬병
	 * @since 2025-05-21
	 */
	private boolean hasNameWhitespace(String name, Errors errors) {
		if (StringUtils.containsWhitespace(name)) {
			AuthValidationError.NAME_WHITESPACE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 이름의 길이가 30자를 초과하는지 확인하고, 초과 시 오류를 등록합니다.
	 *
	 * @param name   검증할 이름 문자열
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-21
	 */
	private void checkNameSize(String name, Errors errors) {
		if (30 < name.length()) {
			AuthValidationError.NAME_SIZE.reject(errors);
		}
	}

	/**
	 * 이름에 영문·한글 이외의 문자가 포함되었는지 검사하고, 포함 시 오류를 등록합니다.
	 *
	 * @param name   검증할 이름 문자열
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-21
	 */
	private void checkNamePattern(String name, Errors errors) {
		if (!NAME_PATTERN.matcher(name).matches()) {
			AuthValidationError.NAME_INVALID_CHAR.reject(errors);
		}
	}

	/**
	 * account가 null 또는 blank인지 확인하고, 빈 값일 경우 오류를 등록합니다.
	 *
	 * @param account 계정 문자열
	 * @param errors  Errors
	 * @return 빈 값일 경우 true, 아닐 경우 false
	 * @since 2025-05-21
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
	 * @since 2025-05-21
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
	 * @since 2025-05-21
	 * @author 박찬병
	 */
	private void checkAccountSize(String account, Errors errors) {
		int length = account.length();
		if (4 > length || length > 20) {
			AuthValidationError.ACCOUNT_SIZE.reject(errors);
		}
	}

}
