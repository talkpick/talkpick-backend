package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.DuplicateCheckReqDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

/**
 * DuplicateCheckReqDto에서
 * field(account/email/nickname)에 따라
 * value 값을 검증하는 Validator.
 *
 * @since 2025-05-15
 */
@Component
public class DuplicateCheckReqDtoValidator implements Validator {

	private static final Pattern EMAIL_PATTERN = Pattern.compile(
		"^\\S+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,6}$"
	);
	private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[A-Za-z가-힣]+$");

	/**
	 * 주어진 클래스가 DuplicateCheckReqDto를 지원하는지 여부를 반환합니다.
	 *
	 * @param clazz 검사 대상 클래스
	 * @return 지원 가능하면 true, 아니면 false
	 * @author 박찬병
	 * @since 2025-05-15
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return DuplicateCheckReqDto.class.isAssignableFrom(clazz);
	}

	/**
	 * DuplicateCheckReqDto의 field 값에 따라
	 * account, email, nickname 검증 로직을 실행합니다.
	 *
	 * @param target  검증 대상 객체
	 * @param errors  검증 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-15
	 */
	@Override
	public void validate(Object target, Errors errors) {
		DuplicateCheckReqDto dto = (DuplicateCheckReqDto) target;
		switch (dto.field()) {
			case ACCOUNT:
				validateAccount(dto.value(), errors);
				break;
			case EMAIL:
				validateEmail(dto.value(), errors);
				break;
			case NICKNAME:
				validateNickname(dto.value(), errors);
				break;
			default:
		}
	}

	/**
	 * ACCOUNT 필드 값의 공통 검증(비어있는지, 공백 포함, 길이)을 수행합니다.
	 *
	 * @param account 검증할 계정 문자열
	 * @param errors  오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-15
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
	 * EMAIL 필드 값의 공통 검증(비어있는지, 공백 포함, 패턴)을 수행합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-15
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
	 * NICKNAME 필드 값의 공통 검증(비어있는지, 공백 포함, 길이, 패턴)을 수행합니다.
	 *
	 * @param nick   검증할 닉네임 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-15
	 */
	private void validateNickname(String nick, Errors errors) {
		if (rejectIfNickEmpty(nick, errors)) {
			return;
		}
		if (rejectIfNickWhitespace(nick, errors)) {
			return;
		}
		rejectIfNickSize(nick, errors);
		rejectIfNickPattern(nick, errors);
	}

	/**
	 * ACCOUNT 값이 비어있는지 확인하고, 비어있으면 오류 등록 후 true를 반환합니다.
	 *
	 * @param account 검증할 계정 문자열
	 * @param errors  오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-15
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
	 * @since 2025-05-15
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
	 * @since 2025-05-15
	 */
	private void rejectIfAccountSize(String account, Errors errors) {
		int len = account.length();
		if (len < 4 || len > 20) {
			AuthValidationError.ACCOUNT_SIZE.reject(errors);
		}
	}

	/**
	 * EMAIL 값이 비어있는지 확인하고, 비어있으면 오류 등록 후 true를 반환합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-15
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
	 * @since 2025-05-15
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
	 * @since 2025-05-15
	 */
	private void rejectIfEmailPattern(String email, Errors errors) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			AuthValidationError.EMAIL_PATTERN.reject(errors);
		}
	}

	/**
	 * NICKNAME 값이 비어있는지 확인하고, 비어있으면 오류 등록 후 true를 반환합니다.
	 *
	 * @param nick   검증할 닉네임 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-15
	 */
	private boolean rejectIfNickEmpty(String nick, Errors errors) {
		if (!StringUtils.hasText(nick)) {
			AuthValidationError.NICKNAME_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * NICKNAME 값에 공백이 포함되어 있는지 확인하고, 포함 시 오류 등록 후 true를 반환합니다.
	 *
	 * @param nick   검증할 닉네임 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-15
	 */
	private boolean rejectIfNickWhitespace(String nick, Errors errors) {
		if (StringUtils.containsWhitespace(nick)) {
			AuthValidationError.NICKNAME_WHITESPACE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * NICKNAME 값의 길이가 20초과인지 확인하여
	 * 조건을 만족하지 않으면 오류를 등록합니다.
	 *
	 * @param nick   검증할 닉네임 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-15
	 */
	private void rejectIfNickSize(String nick, Errors errors) {
		if (nick.length() > 20) {
			AuthValidationError.NICKNAME_SIZE.reject(errors);
		}
	}

	/**
	 * NICKNAME 패턴이 올바른지 확인하고, 올바르지 않으면 오류를 등록합니다.
	 *
	 * @param nick   검증할 닉네임 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-15
	 */
	private void rejectIfNickPattern(String nick, Errors errors) {
		if (!NICKNAME_PATTERN.matcher(nick).matches()) {
			AuthValidationError.NICKNAME_INVALID_CHAR.reject(errors);
		}
	}
}
