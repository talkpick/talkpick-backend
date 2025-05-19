package com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 사용자의 성별을 나타내는 Enum.
 *
 * @since 2025-05-16
 */
@Getter
@AllArgsConstructor
public enum Gender {
	MALE("MALE"), FEMALE("FEMALE"), OTHER("OTHER");

	private final String genderName;
}
