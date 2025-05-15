package com.likelion.backendplus4.talkpick.backend.common.annotation.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * 인증된 사용자 정보를 가져오는 어노테이션
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression =
	"T(com.likelion.backendplus4.talkpick.backend.common.util.security.LoginUserUtil).getPrincipalOrThrow(#this)")
public @interface LoginUser {

}
