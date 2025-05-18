package com.likelion.backendplus4.talkpick.backend.user.presentation.controller.support.validator;

import java.time.LocalDate;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.req.UserUpdateReqDto;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.Gender;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

/**
 * UserUpdateReqDto에 담긴 사용자 프로필 수정 요청 정보를 검증하는 Validator.
 *
 * @since 2025-05-16
 */
@Component
public class UserUpdateReqDtoValidator implements Validator {

    private static final Pattern EMAIL_PATTERN    = Pattern.compile("^\\S+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,6}$");
    private static final Pattern NAME_PATTERN     = Pattern.compile("^[A-Za-z가-힣]+$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[A-Za-z가-힣]+$");

    @Override
    public boolean supports(Class<?> clazz) {
        return UserUpdateReqDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserUpdateReqDto dto = (UserUpdateReqDto) target;
        validateName(dto.name(), errors);
        validateNickName(dto.nickName(), errors);
        validateEmail(dto.email(), errors);
        validateGender(dto.gender(), errors);
        validateBirthday(dto.birthDay(), errors);
    }

    /**
     * 프로필 수정 시 이름 필드를 검증합니다.
     *
     * @param name 이름
     * @param errors 오류 객체
     * @since 2025-05-18
     */
    private void validateName(String name, Errors errors) {
        if (isNameEmpty(name, errors)) {
            return;
        }
        if (hasNameWhitespace(name, errors)) {
            return;
        }
        checkNameSize(name, errors);
        checkNamePattern(name, errors);
    }

	/**
	 * 프로필 수정 시 닉네임 필드를 검증합니다.
	 *
	 * @param nick 닉네임
	 * @param errors 오류 객체
	 * @since 2025-05-18
	 */
	private void validateNickName(String nick, Errors errors) {
		if (isNickEmpty(nick, errors)) {
			return;
		}
		if (hasNickWhitespace(nick, errors)) {
			return;
		}
		checkNickSize(nick, errors);
		checkNickNamePattern(nick, errors);
	}

	/**
	 * 프로필 수정 시 이메일 필드를 검증합니다.
	 *
	 * @param email 이메일
	 * @param errors 오류 객체
	 * @since 2025-05-18
	 */
	private void validateEmail(String email, Errors errors) {
		if (isEmailEmpty(email, errors)) {
			return;
		}
		if (hasEmailWhitespace(email, errors)) {
			return;
		}
		checkEmailPattern(email, errors);

	}

	/**
	 * 프로필 수정 시 생년월일 필드를 검증합니다.
	 *
	 * @param birthday 생년월일
	 * @param errors 오류 객체
	 * @since 2025-05-18
	 */
	private void validateBirthday(LocalDate birthday, Errors errors) {
		if (isBirthdayEmpty(birthday, errors)) {
			return;
		}
		checkBirthdayFuture(birthday, errors);
	}

	/**
	 * 프로필 수정 시 성별 필드를 검증합니다.
	 *
	 * @param gender 성별
	 * @param errors 오류 객체
	 * @since 2025-05-18
	 */
	private void validateGender(Gender gender, Errors errors) {
		if (gender == null) {
			AuthValidationError.GENDER_EMPTY.reject(errors);
		}
	}

    /**
     * 이름이 비어있는지 확인합니다.
     *
     * @param name 이름
     * @param errors 오류 객체
     * @return 비어있으면 true, 아니면 false
     * @since 2025-05-18
     */
    private boolean isNameEmpty(String name, Errors errors) {
        if (!StringUtils.hasText(name)) {
            AuthValidationError.NAME_EMPTY.reject(errors);
            return true;
        }
        return false;
    }

    /**
     * 이름에 공백 문자가 포함되어 있는지 확인합니다.
     *
     * @param name 이름
     * @param errors 오류 객체
     * @return 공백 포함 시 true, 아니면 false
     * @since 2025-05-18
     */
    private boolean hasNameWhitespace(String name, Errors errors) {
        if (StringUtils.containsWhitespace(name)) {
            AuthValidationError.NAME_WHITESPACE.reject(errors);
            return true;
        }
        return false;
    }

    /**
     * 이름의 길이가 최대 허용 길이를 초과하는지 확인합니다.
     *
     * @param name 이름
     * @param errors 오류 객체
     * @since 2025-05-18
     */
    private void checkNameSize(String name, Errors errors) {
        if (name.length() > 30) {
            AuthValidationError.NAME_SIZE.reject(errors);
        }
    }

    /**
     * 이름이 허용된 문자 패턴에 맞는지 확인합니다.
     *
     * @param name 이름
     * @param errors 오류 객체
     * @since 2025-05-18
     */
    private void checkNamePattern(String name, Errors errors) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            AuthValidationError.NAME_INVALID_CHAR.reject(errors);
        }
    }


    /**
     * 닉네임이 비어있는지 확인합니다.
     *
     * @param nick 닉네임
     * @param errors 오류 객체
     * @return 비어있으면 true, 아니면 false
     * @since 2025-05-18
     */
    private boolean isNickEmpty(String nick, Errors errors) {
        if (!StringUtils.hasText(nick)) {
            AuthValidationError.NICKNAME_EMPTY.reject(errors);
            return true;
        }
        return false;
    }

    /**
     * 닉네임에 공백 문자가 포함되어 있는지 확인합니다.
     *
     * @param nick 닉네임
     * @param errors 오류 객체
     * @return 공백 포함 시 true, 아니면 false
     * @since 2025-05-18
     */
    private boolean hasNickWhitespace(String nick, Errors errors) {
        if (StringUtils.containsWhitespace(nick)) {
            AuthValidationError.NICKNAME_WHITESPACE.reject(errors);
            return true;
        }
        return false;
    }

    /**
     * 닉네임의 길이가 최대 허용 길이를 초과하는지 확인합니다.
     *
     * @param nick 닉네임
     * @param errors 오류 객체
     * @since 2025-05-18
     */
    private void checkNickSize(String nick, Errors errors) {
        if (nick.length() > 20) {
            AuthValidationError.NICKNAME_SIZE.reject(errors);
        }
    }

    /**
     * 닉네임이 허용된 문자 패턴에 맞는지 확인합니다.
     *
     * @param nick 닉네임
     * @param errors 오류 객체
     * @since 2025-05-18
     */
    private void checkNickNamePattern(String nick, Errors errors) {
        if (!NICKNAME_PATTERN.matcher(nick).matches()) {
            AuthValidationError.NICKNAME_INVALID_CHAR.reject(errors);
        }
    }



    /**
     * 이메일이 비어있는지 확인합니다.
     *
     * @param email 이메일
     * @param errors 오류 객체
     * @return 비어있으면 true, 아니면 false
     * @since 2025-05-18
     */
    private boolean isEmailEmpty(String email, Errors errors) {
        if (!StringUtils.hasText(email)) {
            AuthValidationError.EMAIL_EMPTY.reject(errors);
            return true;
        }
        return false;
    }

    /**
     * 이메일에 공백 문자가 포함되어 있는지 확인합니다.
     *
     * @param email 이메일
     * @param errors 오류 객체
     * @return 공백 포함 시 true, 아니면 false
     * @since 2025-05-18
     */
    private boolean hasEmailWhitespace(String email, Errors errors) {
        if (StringUtils.containsWhitespace(email)) {
            AuthValidationError.EMAIL_WHITESPACE.reject(errors);
            return true;
        }
        return false;
    }

    /**
     * 이메일이 허용된 패턴에 맞는지 확인합니다.
     *
     * @param email 이메일
     * @param errors 오류 객체
     * @since 2025-05-18
     */
    private void checkEmailPattern(String email, Errors errors) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            AuthValidationError.EMAIL_PATTERN.reject(errors);
        }
    }


    /**
     * 생년월일이 비어있는지 확인합니다.
     *
     * @param birthday 생년월일
     * @param errors 오류 객체
     * @return 비어있으면 true, 아니면 false
     * @since 2025-05-18
     */
    private boolean isBirthdayEmpty(LocalDate birthday, Errors errors) {
        if (birthday == null) {
            AuthValidationError.BIRTHDAY_EMPTY.reject(errors);
            return true;
        }
        return false;
    }

    /**
     * 생년월일이 현재 날짜 이후인지 확인합니다.
     *
     * @param birthday 생년월일
     * @param errors 오류 객체
     * @since 2025-05-18
     */
    private void checkBirthdayFuture(LocalDate birthday, Errors errors) {
        if (birthday.isAfter(LocalDate.now())) {
            AuthValidationError.BIRTHDAY_FUTURE.reject(errors);
        }
    }
}
