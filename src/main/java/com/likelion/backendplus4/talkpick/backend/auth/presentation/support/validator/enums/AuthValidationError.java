package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums;

import org.springframework.validation.Errors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AuthValidationError {
	ACCOUNT_EMPTY("account", "account.empty", "계정은 필수입니다."),
	ACCOUNT_WHITESPACE("account", "account.whitespace", "계정에 공백은 허용되지 않습니다."),
	ACCOUNT_SIZE("account", "account.size", "계정은 4자 이상, 20자 이하로 입력해야 합니다."),

	PASSWORD_EMPTY("password", "password.empty", "비밀번호는 필수입니다."),
	PASSWORD_WHITESPACE("password", "password.whitespace", "비밀번호에 공백은 허용되지 않습니다."),
	PASSWORD_SIZE("password", "password.size", "비밀번호는 8자 이상, 16자 이하로 입력해야 합니다."),
	PASSWORD_PATTERN("password", "password.pattern", "비밀번호는 대문자·소문자·숫자·특수문자를 모두 포함해야 합니다."),

	NAME_EMPTY("name", "name.empty", "이름은 필수입니다."),
	NAME_WHITESPACE("name", "name.whitespace", "이름에 공백은 허용되지 않습니다."),
	NAME_SIZE("name", "name.size", "이름은 최대 30자까지 입력할 수 있습니다."),
	NAME_INVALID_CHAR("name", "name.invalid.char", "이름은 영문과 한글만 허용됩니다."),

	NICKNAME_EMPTY("nickName", "nickName.empty", "닉네임이 비어 있습니다."),
	NICKNAME_WHITESPACE("nickName", "nickName.whitespace", "닉네임에 공백은 허용되지 않습니다."),
	NICKNAME_SIZE("nickName", "nickName.size", "닉네임은 최대 20자까지 입력할 수 있습니다."),
	NICKNAME_INVALID_CHAR("nickName", "nickName.invalid.char", "닉네임은 영문과 한글만 허용됩니다."),

	EMAIL_EMPTY("email", "email.empty", "이메일은 필수입니다."),
	EMAIL_WHITESPACE("email", "email.whitespace", "이메일에 공백은 허용되지 않습니다."),
	EMAIL_PATTERN("email", "email.pattern", "올바른 이메일 형식이 아닙니다."),

	CODE_EMPTY("code", "code.empty", "인증 코드는 필수입니다."),
	CODE_INVALID_FORMAT("code", "code.invalid.format", "인증 코드는 숫자 6자리여야 합니다."),

	GENDER_EMPTY("gender", "gender.empty", "성별은 필수입니다."),

	BIRTHDAY_EMPTY("birthDay", "birthDay.empty", "생년월일은 필수입니다."),
	BIRTHDAY_FUTURE("birthDay", "birthDay.future", "생년월일은 현재 날짜 이전이어야 합니다."),

	ACCESS_TOKEN_EMPTY("accessToken", "accessToken.empty", "accessToken은 필수입니다."),
	REFRESH_TOKEN_EMPTY("refreshToken", "refreshToken.empty", "refreshToken은 필수합니다."),
	TEMP_TOKEN_EMPTY("tempToken", "tempToken.empty", "tempToken은 필수입니다.");


	private final String field;
	private final String code;
	private final String message;

	public void reject(Errors errors) {
		errors.rejectValue(field, code, message);
	}
}
