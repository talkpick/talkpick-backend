package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import java.util.regex.Pattern;
import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.Gender;

/**
 * SignUpDto에 담긴 회원 가입 요청 정보를 검증하는 Validator.
 * 공통 계정·비밀번호 검증 후 이름·닉네임·이메일·성별·생년월일을 검증합니다.
 *
 * @author 박찬병
 * @since 2025-05-12
 * @modified 2025-05-14
 */
@Component
public class SignUpDtoValidator extends AbstractAuthValidator<SignUpDto> {

	/** 이메일 패턴: 공백 불가, 로컬파트·서브도메인·TLD 검증 */
	private static final Pattern EMAIL_PATTERN = Pattern.compile(
		"^\\S+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,6}$"
	);
	/** 이름/닉네임에 허용할 문자 패턴: 영문 대소문자, 한글만 허용 */
	private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z가-힣]+$");
	private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[A-Za-z가-힣]+$");

	public SignUpDtoValidator() {
		super(SignUpDto.class);
	}

	@Override
	protected String extractAccount(SignUpDto dto) {
		return dto.account();
	}

	@Override
	protected String extractPassword(SignUpDto dto) {
		return dto.password();
	}

	@Override
	protected void validateAdditional(SignUpDto dto, Errors errors) {
		validateName(dto.name(), errors);
		validateNickName(dto.nickName(), errors);
		validateEmail(dto.email(), errors);
		validateGender(dto.gender(), errors);
		validateBirthday(dto.birthDay(), errors);
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
	 * @since 2025-05-13
	 * @modified 2025-05-14
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
	 * 닉네임 값을 검증합니다.
	 *  - null 또는 빈 문자열 불가
	 *  - 공백 문자 불가
	 *  - 최대 20자
	 *
	 * @param nick 사용자의 닉네임
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-13
	 * @modified 2025-05-14
	 */
	private void validateNickName(String nick, Errors errors) {
		if (isNickEmpty(nick, errors))
			return;
		if (hasNickWhitespace(nick, errors))
			return;
		checkNickSize(nick, errors);
		checkNickNamePattern(nick, errors);
	}

	/**
	 * 이메일 값을 검증합니다.
	 *  - null 또는 빈 문자열 불가
	 *  - 공백 문자 불가
	 *  - 이메일 형식(로컬파트@도메인) 검증
	 *
	 * @param email 사용자의 이메일
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-13
	 * @modified 2025-05-14
	 */
	private void validateEmail(String email, Errors errors) {
		if (isEmailEmpty(email, errors))
			return;
		if (hasEmailWhitespace(email, errors))
			return;
		checkEmailPattern(email, errors);
	}

	/**
	 * 성별 값을 검증합니다.
	 *  - null 허용되지 않음
	 *
	 * @param gender 사용자의 성별
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-13
	 * @modified 2025-05-14
	 */
	private void validateGender(Gender gender, Errors errors) {
		isGenderEmpty(gender, errors);
	}

	/**
	 * 생년월일 값을 검증합니다.
	 *  - null 허용되지 않음
	 *  - 미래 날짜는 허용되지 않음
	 *
	 * @param birthday 사용자의 생년월일
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-13
	 * @modified 2025-05-14
	 */
	private void validateBirthday(LocalDate birthday, Errors errors) {
		if (isBirthdayEmpty(birthday, errors))
			return;
		checkBirthdayFuture(birthday, errors);
	}

	/**
	 * 이름이 null 또는 blank인지 확인하고, 빈 값일 경우 오류를 등록합니다.
	 *
	 * @param name   검증할 이름 문자열
	 * @param errors Errors 객체
	 * @return 빈 값일 경우 true, 아닐 경우 false
	 * @author 박찬병
	 * @since 2025-05-14
	 * @modified 2025-05-14
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
	 * @since 2025-05-14
	 * @modified 2025-05-14
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
	 * @since 2025-05-14
	 * @modified 2025-05-14
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
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 */
	private void checkNamePattern(String name, Errors errors) {
		if (!NAME_PATTERN.matcher(name).matches()) {
			AuthValidationError.NAME_INVALID_CHAR.reject(errors);
		}
	}

	/**
	 * 닉네임이 null 또는 blank인지 확인하고, 빈 값일 경우 오류를 등록합니다.
	 *
	 * @param nick   검증할 닉네임 문자열
	 * @param errors Errors 객체
	 * @return 빈 값일 경우 true, 아닐 경우 false
	 * @author 박찬병
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 */
	private boolean isNickEmpty(String nick, Errors errors) {
		if (!StringUtils.hasText(nick)) {
			AuthValidationError.NICKNAME_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 닉네임에 공백이 포함되었는지 확인하고, 포함된 경우 오류를 등록합니다.
	 *
	 * @param nick   검증할 닉네임 문자열
	 * @param errors Errors 객체
	 * @return 공백이 포함된 경우 true, 아니면 false
	 * @author 박찬병
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 */
	private boolean hasNickWhitespace(String nick, Errors errors) {
		if (StringUtils.containsWhitespace(nick)) {
			AuthValidationError.NICKNAME_WHITESPACE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 닉네임의 길이가 20자를 초과하는지 확인하고, 초과 시 오류를 등록합니다.
	 *
	 * @param nick   검증할 닉네임 문자열
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 */
	private void checkNickSize(String nick, Errors errors) {
		if (20 < nick.length()) {
			AuthValidationError.NICKNAME_SIZE.reject(errors);
		}
	}

	/**
	 * 닉네임에 영문·한글 이외의 문자가 포함되었는지 검사하고, 포함 시 오류를 등록합니다.
	 *
	 * @param nick   검증할 이름 문자열
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 */
	private void checkNickNamePattern(String nick, Errors errors) {
		if (!NICKNAME_PATTERN.matcher(nick).matches()) {
			AuthValidationError.NICKNAME_INVALID_CHAR.reject(errors);
		}
	}

	/**
	 * 이메일이 null 또는 blank인지 확인하고, 빈 값일 경우 오류를 등록합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors Errors 객체
	 * @return 빈 값일 경우 true, 아닐 경우 false
	 * @author 박찬병
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 */
	private boolean isEmailEmpty(String email, Errors errors) {
		if (!StringUtils.hasText(email)) {
			AuthValidationError.EMAIL_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 이메일에 공백이 포함되었는지 확인하고, 포함된 경우 오류를 등록합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors Errors 객체
	 * @return 공백이 포함된 경우 true, 아니면 false
	 * @author 박찬병
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 */
	private boolean hasEmailWhitespace(String email, Errors errors) {
		if (StringUtils.containsWhitespace(email)) {
			AuthValidationError.EMAIL_WHITESPACE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 이메일 형식이 올바른지 정규식으로 검증하고, 올바르지 않으면 오류를 등록합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 */
	private void checkEmailPattern(String email, Errors errors) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			AuthValidationError.EMAIL_PATTERN.reject(errors);
		}
	}

	/**
	 * 성별이 null인지 확인하고, null일 경우 오류를 등록합니다.
	 *
	 * @param gender 검증할 성별
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @modified 2025-05-14
	 * @since 2025-05-14
	 */
	private void isGenderEmpty(Gender gender, Errors errors) {
		if (null == gender) {
			AuthValidationError.GENDER_EMPTY.reject(errors);
		}
	}

	/**
	 * 생년월일이 null인지 확인하고, null일 경우 오류를 등록합니다.
	 *
	 * @param birthday 검증할 생년월일
	 * @param errors   Errors 객체
	 * @return null일 경우 true, 아닐 경우 false
	 * @author 박찬병
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 */
	private boolean isBirthdayEmpty(LocalDate birthday, Errors errors) {
		if (null == birthday) {
			AuthValidationError.BIRTHDAY_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 생년월일이 미래 날짜인지 확인하고, 미래일 경우 오류를 등록합니다.
	 *
	 * @param birthday 검증할 생년월일
	 * @param errors   Errors 객체
	 * @author 박찬병
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 */
	private void checkBirthdayFuture(LocalDate birthday, Errors errors) {
		if (birthday.isAfter(LocalDate.now())) {
			AuthValidationError.BIRTHDAY_FUTURE.reject(errors);
		}
	}
}