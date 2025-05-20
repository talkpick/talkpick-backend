package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.check.CheckNicknameDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

@Component
public class CheckNicknameValidator implements Validator {

	@Value("#{T(java.util.regex.Pattern).compile('${validator.pattern.nickname}')}")
	private Pattern NICKNAME_PATTERN;

	@Override
	public boolean supports(Class<?> clazz) {
		return CheckNicknameDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CheckNicknameDto dto = (CheckNicknameDto) target;
		validateNickname(dto.nickName(), errors);
	}

	/**
	 * NICKNAME 필드 값의 공통 검증(비어있는지, 공백 포함, 길이, 패턴)을 수행합니다.
	 *
	 * @param nickname   검증할 닉네임 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private void validateNickname(String nickname, Errors errors) {
		if (rejectIfNickEmpty(nickname, errors)) {
			return;
		}
		if (rejectIfNickWhitespace(nickname, errors)) {
			return;
		}
		rejectIfNickSize(nickname, errors);
		rejectIfNickPattern(nickname, errors);
	}

	/**
	 * NICKNAME 값이 비어있는지 확인하고, 비어있으면 오류 등록 후 true를 반환합니다.
	 *
	 * @param nickname   검증할 닉네임 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private boolean rejectIfNickEmpty(String nickname, Errors errors) {
		if (!StringUtils.hasText(nickname)) {
			AuthValidationError.NICKNAME_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * NICKNAME 값에 공백이 포함되어 있는지 확인하고, 포함 시 오류 등록 후 true를 반환합니다.
	 *
	 * @param nickname   검증할 닉네임 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private boolean rejectIfNickWhitespace(String nickname, Errors errors) {
		if (StringUtils.containsWhitespace(nickname)) {
			AuthValidationError.NICKNAME_WHITESPACE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * NICKNAME 값의 길이가 20초과인지 확인하여
	 * 조건을 만족하지 않으면 오류를 등록합니다.
	 *
	 * @param nickname   검증할 닉네임 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private void rejectIfNickSize(String nickname, Errors errors) {
		if (nickname.length() > 20) {
			AuthValidationError.NICKNAME_SIZE.reject(errors);
		}
	}

	/**
	 * NICKNAME 패턴이 올바른지 확인하고, 올바르지 않으면 오류를 등록합니다.
	 *
	 * @param nickname   검증할 닉네임 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-18
	 */
	private void rejectIfNickPattern(String nickname, Errors errors) {
		if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
			AuthValidationError.NICKNAME_INVALID_CHAR.reject(errors);
		}
	}
}
