package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller;

import static com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse.*;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator.NewsIdConstraint;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Validated  // 클래스 레벨에 추가 필요
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/news")
public class NewsInfoDetailProviderController {
	private final NewsInfoDetailProviderUseCase newsInfoDetailProviderUseCase;
	private final NewsViewCountIncreaseUseCase newsViewCountIncreaseUseCase;

	/**
	 * 뉴스 ID를 기반으로 뉴스 상세 정보를 조회하는 API 엔드포인트입니다.
	 *
	 * 1. 뉴스 ID 형식 검증 (KM, DA, KH 접두사 + 숫자)
	 * 2. 클라이언트 IP 주소 획득
	 * 3. 조회수 증가 (중복 조회 제외)
	 * 4. 뉴스 상세 정보 조회 및 응답
	 *
	 * @param id 조회할 뉴스의 ID (형식: KM123, DA456, KH789)
	 * @param request HTTP 요청 객체 (IP 주소 획득용)
	 * @return 뉴스 상세 정보가 포함된 API 응답
	 * @throws jakarta.validation.ConstraintViolationException 뉴스 ID 형식이 잘못된 경우
	 * @since 2025-05-19 최초 작성
	 * @author 양병학
	 * @modified 2025-05-25 양병학
	 *  - 뉴스 ID validation 추가
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<NewsInfoDetailResponse>> getNewsInfoDetailsByArticleId(
		@PathVariable @NewsIdConstraint String id,  // @NewsIdConstraint 추가
		HttpServletRequest request) {

		String ipAddress = request.getRemoteAddr();

		NewsInfoDetailResponse newsDetail = newsInfoDetailProviderUseCase.getNewsInfoDetailByNewsId(id);

		newsViewCountIncreaseUseCase.increaseViewCount(id, ipAddress, newsDetail.category(), newsDetail.publishDate());

		return success(newsDetail);
	}
}