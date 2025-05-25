package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.PopularNewsResponseMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.PopularNewsUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.PopularNewsPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PopularNewsService implements PopularNewsUseCase {

	private final PopularNewsPort popularNewsPort;
	private final NewsDetailProviderPort newsDetailProviderPort;

	private static final List<String> CATEGORIES = Arrays.asList(
		"전체", "정치", "경제", "사회", "국제", "연예", "스포츠"
	);

	@Override
	@Cacheable(value = "popularNews", key = "'allCategories'")
	public List<PopularNewsResponse> getTopNewsByAllCategories() {
		List<PopularNewsResponse> result = new ArrayList<>();

		for (String category : CATEGORIES) {
			String topNewsId = popularNewsPort.getTop1NewsId(category);

			if (topNewsId != null) {
				NewsInfoDetail newsDetail = newsDetailProviderPort.getNewsInfoDetailsByArticleId(topNewsId);
				PopularNewsResponse response = PopularNewsResponseMapper.toResponse(newsDetail);
				result.add(response);
			}
		}

		return result;
	}
}