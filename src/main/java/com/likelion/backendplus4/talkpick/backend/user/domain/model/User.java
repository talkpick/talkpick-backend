package com.likelion.backendplus4.talkpick.backend.user.domain.model;

import java.time.LocalDate;

import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보를 담는 도메인 모델 클래스입니다.
 * 사용자 ID, 성별, 생년월일, 이름, 닉네임, 이메일 정보를 포함합니다.
 *
 * @since 2025-05-16
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	private Long userId;
	private Gender gender;
	private LocalDate birthday;
	private String name;
	private String nickName;
	private String email;
}
