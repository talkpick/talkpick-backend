package com.likelion.backendplus4.talkpick.backend.user.presentation.controller.support.mapper;

import com.likelion.backendplus4.talkpick.backend.user.domain.model.User;
import com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.res.UserInfoResDto;

/**
 * 사용자 도메인 모델을 UserInfoDto로 변환하는 매퍼 클래스입니다.
 *
 * @since 2025-05-16
 */
public class UserInfoDtoMapper {

	/**
	 * User 도메인 객체를 UserInfoDto로 변환합니다.
	 *
	 * @param user 변환할 사용자 도메인 객체
	 * @return 변환된 UserInfoDto 객체
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	public static UserInfoResDto toDtoFromUser(User user) {
		return UserInfoResDto.builder()
			.userId(user.getUserId())
			.gender(user.getGender().getGenderName())
			.birthday(user.getBirthday())
			.name(user.getName())
			.nickName(user.getNickName())
			.email(user.getEmail())
			.build();
	}
}
