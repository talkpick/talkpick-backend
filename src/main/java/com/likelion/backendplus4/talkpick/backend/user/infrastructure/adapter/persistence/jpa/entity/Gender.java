package com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
	MALE("남성"), FEMALE("여성"), OTHER("그외");

	private final String genderName;
}
