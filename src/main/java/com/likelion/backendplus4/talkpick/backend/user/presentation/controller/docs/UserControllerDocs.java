package com.likelion.backendplus4.talkpick.backend.user.presentation.controller.docs;

import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoComplete;
import com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.req.UserUpdateReqDto;
import com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.res.UserInfoResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(
	name = "User",
	description = "사용자 프로필 조회·수정·탈퇴 및 스크랩 조회 API"
)
public interface UserControllerDocs {

	@Operation(
		summary = "내 프로필 조회",
		description = "로그인된 사용자의 프로필 정보를 반환합니다.",
		security = @SecurityRequirement(name = "BearerAuth")
	)
	ResponseEntity<ApiResponse<UserInfoResDto>> getProfile(
		@Parameter(
			in = ParameterIn.HEADER,
			description = "로그인된 사용자 ID (JWT로부터 추출됨)",
			hidden = true
		)
		Long userId
	);

	@Operation(
		summary = "내 스크랩 내역 조회",
		description = "로그인된 사용자의 스크랩한 뉴스 정보를 리스트로 반환합니다.",
		security = @SecurityRequirement(name = "BearerAuth")
	)
	ResponseEntity<ApiResponse<List<NewsInfoComplete>>> getMyScrap(
		@Parameter(
			in = ParameterIn.HEADER,
			description = "로그인된 사용자 ID (JWT로부터 추출됨)",
			hidden = true
		)
		Long userId
	);

	@Operation(
		summary = "내 프로필 수정",
		description = "로그인된 사용자의 프로필 정보를 업데이트합니다.",
		security = @SecurityRequirement(name = "BearerAuth")
	)
	ResponseEntity<ApiResponse<Void>> updateProfile(
		@Parameter(
			in = ParameterIn.HEADER,
			description = "로그인된 사용자 ID (JWT로부터 추출됨)",
			hidden = true
		)
		Long userId,
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "수정할 프로필 정보",
			required = true
		)
		UserUpdateReqDto updateReqDto
	);

	@Operation(
		summary = "회원 탈퇴",
		description = "로그인된 사용자의 계정을 삭제합니다.",
		security = @SecurityRequirement(name = "BearerAuth")
	)
	ResponseEntity<ApiResponse<Void>> deleteAccount(
		@Parameter(
			in = ParameterIn.HEADER,
			description = "로그인된 사용자 ID (JWT로부터 추출됨)",
			hidden = true
		)
		Long userId
	);
}