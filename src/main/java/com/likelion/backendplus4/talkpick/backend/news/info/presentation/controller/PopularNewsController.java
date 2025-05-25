// presentation/controller/PopularNewsController.java
package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller;

import static com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.PopularNewsUseCase;

import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 인기뉴스 조회 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/news")
public class PopularNewsController {

	private final PopularNewsUseCase popularNewsUseCase;

	/**
	 * 전체 및 카테고리별 Top1 인기뉴스 조회 API
	 *
	 * @return 7개 카테고리별 Top1 뉴스 목록
	 */
	@GetMapping("/top-viewed")
	public ResponseEntity<ApiResponse<List<PopularNewsResponse>>> getTopViewedNews() {
		List<PopularNewsResponse> topNews = popularNewsUseCase.getTopNewsByAllCategories();
		return success(topNews);
	}
}