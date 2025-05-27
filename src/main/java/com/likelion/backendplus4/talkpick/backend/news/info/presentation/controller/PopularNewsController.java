package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller;

import static com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.PopularNewsUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.mapper.CategoryMapper;

import lombok.RequiredArgsConstructor;

/**
 * 인기뉴스 조회 API 컨트롤러입니다.
 *
 * @since 2025-05-27
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/news")
public class PopularNewsController {

	private final PopularNewsUseCase popularNewsUseCase;
	private final CategoryMapper categoryMapper;

	/**
	 * "전체" 카테고리 Top1 인기뉴스 조회 API입니다.
	 *
	 * 메인 페이지에서 호출되며, 모든 뉴스 중에서 가장 인기 있는 뉴스 1개를 반환합니다.
	 * "전체" 카테고리 Redis 랭킹(news:ranking:전체)에서 Top1을 조회합니다.
	 *
	 * 캐싱 흐름:
	 * 1. "전체" 카테고리 해시값 비교로 순위 변화 감지
	 * 2. 변화 없으면 Spring Cache에서 즉시 반환 (0.000초)
	 * 3. 변화 감지 시 캐시 무효화 → Redis topNews:전체 조회 → DB 조회 순서
	 *
	 * @return "전체" 카테고리의 Top1 뉴스
	 * @since 2025-05-27
	 * @author 양병학
	 */
	@GetMapping("/top-viewed")
	public ResponseEntity<ApiResponse<PopularNewsResponse>> getAllTopViewedNews() {
		PopularNewsResponse topNews = popularNewsUseCase.getTopNewsFromAllCategories();
		return success(topNews);
	}

	/**
	 * 특정 카테고리 인기뉴스 조회 API입니다.
	 *
	 * 각 카테고리별 전용 페이지에서 호출되며, 해당 카테고리의 Top1 뉴스 1개를 반환합니다.
	 * URL 경로의 영어 카테고리명을 검증 후 한글로 변환하여 해당 카테고리 Redis 랭킹에서 조회합니다.
	 *
	 * API 별 Redis 조회 대상:
	 * - GET /top-viewed/politics     → news:ranking:정치 에서 Top1 조회
	 * - GET /top-viewed/society      → news:ranking:사회 에서 Top1 조회
	 * - GET /top-viewed/economy      → news:ranking:경제 에서 Top1 조회
	 * - GET /top-viewed/sports       → news:ranking:스포츠 에서 Top1 조회
	 * - GET /top-viewed/international → news:ranking:국제 에서 Top1 조회
	 * - GET /top-viewed/entertainment → news:ranking:연예 에서 Top1 조회
	 * - GET /top-viewed/all          → news:ranking:전체 에서 Top1 조회
	 *
	 * 캐싱 흐름:
	 * 1. 해당 카테고리의 해시값 비교로 순위 변화 감지
	 * 2. 변화 없으면 Spring Cache에서 즉시 반환
	 * 3. 변화 감지 시 해당 카테고리만 캐시 무효화 → Redis/DB 조회
	 *
	 * @param category 뉴스 카테고리 (영어명) - validation으로 검증됨 (다른 브랜치에서 추가예정)
	 * @return 해당 카테고리의 Top1 인기뉴스
	 * @since 2025-05-27
	 * @author 양병학
	 */
	@GetMapping("/top-viewed/{category}")
	public ResponseEntity<ApiResponse<PopularNewsResponse>> getTopViewedNewsByCategory(@PathVariable String category) {
		String koreanCategoryName = categoryMapper.toKoreanCategory(category);
		PopularNewsResponse topNews = popularNewsUseCase.getTopNewsByCategory(koreanCategoryName);
		return success(topNews);
	}
}