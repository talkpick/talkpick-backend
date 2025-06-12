package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.ClientInfoPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsViewCountPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoMetadata;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoViewCount;

@ExtendWith(MockitoExtension.class)
class ConcurrencyTest {

	@InjectMocks
	private NewsInfoDetailProviderService newsInfoDetailProviderService;

	@Mock
	private NewsDetailProviderPort newsDetailProviderPort;

	@Mock
	private NewsViewCountPort newsViewCountPort;

	@Mock
	private NewsViewCountIncreaseUseCase newsViewCountIncreaseUseCase;

	@Mock
	private ClientInfoPort clientInfoPort;

	@Test
	@DisplayName("동시성 문제 확인: 같은 뉴스를 동시에 조회할 때 조회수 누락 발생")
	void testConcurrentViewCountProblem() throws InterruptedException {

		String newsId = "TEST_NEWS_001";
		int threadCount = 10;

		// Mock 설정
		NewsInfoMetadata metadata = NewsInfoMetadata.builder()
			.newsId(newsId)
			.category("테스트")
			.pubDate(LocalDateTime.now())
			.build();

		when(newsDetailProviderPort.getNewsInfoMetadataByArticleId(newsId))
			.thenReturn(Optional.of(metadata));

		// 초기 조회수 10
		when(newsViewCountPort.getCurrentViewCount(newsId)).thenReturn(10L);

		// IP 중복 체크 비활성화 (모든 요청이 새로운 IP에서 온다고 가정)
		when(newsViewCountPort.hasViewHistory(anyString(), anyString())).thenReturn(false);

		// 각 스레드마다 다른 IP 반환
		AtomicInteger ipCounter = new AtomicInteger(0);
		when(clientInfoPort.getClientIpAddress())
			.thenAnswer(invocation -> "192.168.1." + ipCounter.incrementAndGet());

		// 10개 스레드가 동시에 조회수 증가 요청
		List<CompletableFuture<NewsInfoViewCount>> futures = new ArrayList<>();

		for (int i = 0; i < threadCount; i++) {
			CompletableFuture<NewsInfoViewCount> future = CompletableFuture.supplyAsync(() -> {
				return newsInfoDetailProviderService.getNewsInfoViewCount(newsId);
			});
			futures.add(future);
		}

		// 스레드대기
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		// 결과 수집 분석
		List<NewsInfoViewCount> results = futures.stream()
			.map(CompletableFuture::join)
			.collect(Collectors.toList());

		// 조회수 출력
		System.out.println("=== 동시성 테스트 결과 ===");
		for (int i = 0; i < results.size(); i++) {
			System.out.printf("스레드 %d: 조회수 = %d%n", i + 1, results.get(i).getViewCount());
		}

		// 조회수 분석
		List<Long> viewCounts = results.stream()
			.map(NewsInfoViewCount::getViewCount)
			.collect(Collectors.toList());

		Set<Long> uniqueViewCounts = new HashSet<>(viewCounts);

		System.out.printf("총 요청 수: %d%n", threadCount);
		System.out.printf("유니크한 조회수 개수: %d%n", uniqueViewCounts.size());
		System.out.printf("예상 조회수 범위: %d ~ %d%n", 11, 10 + threadCount);
		System.out.printf("실제 조회수 범위: %d ~ %d%n",
			viewCounts.stream().min(Long::compareTo).orElse(0L),
			viewCounts.stream().max(Long::compareTo).orElse(0L));

		// 동시성 문제 확인
		if (uniqueViewCounts.size() < threadCount) {
			System.out.println("🚨 동시성 문제 발견: 일부 스레드가 같은 조회수를 받았습니다!");

			// 중복 조회수 찾기
			Map<Long, Long> countMap = viewCounts.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

			countMap.entrySet().stream()
				.filter(entry -> entry.getValue() > 1)
				.forEach(entry ->
					System.out.printf("조회수 %d가 %d번 중복%n", entry.getKey(), entry.getValue())
				);
		} else {
			System.out.println("동시성 처리 성공: 모든 스레드가 서로 다른 조회수");
		}

		// increaseViewCount 호출 횟수 확인
		verify(newsViewCountIncreaseUseCase, times(threadCount)).increaseViewCount(
			eq(newsId), anyLong(), eq("테스트"), any(LocalDateTime.class)
		);
	}

	@Test
	@DisplayName("동시성이슈 조회수 중복 확인")
	void testSimpleConcurrencyCheck() throws InterruptedException {
		// Given
		String newsId = "SIMPLE_TEST";

		NewsInfoMetadata metadata = NewsInfoMetadata.builder()
			.newsId(newsId)
			.category("테스트")
			.pubDate(LocalDateTime.now())
			.build();

		when(newsDetailProviderPort.getNewsInfoMetadataByArticleId(newsId))
			.thenReturn(Optional.of(metadata));
		when(newsViewCountPort.getCurrentViewCount(newsId)).thenReturn(5L);
		when(newsViewCountPort.hasViewHistory(anyString(), anyString())).thenReturn(false);
		when(clientInfoPort.getClientIpAddress()).thenReturn("192.168.1.100");

		// 2개 스레드만으로 간단히 테스트
		CompletableFuture<NewsInfoViewCount> future1 = CompletableFuture.supplyAsync(() ->
			newsInfoDetailProviderService.getNewsInfoViewCount(newsId)
		);

		CompletableFuture<NewsInfoViewCount> future2 = CompletableFuture.supplyAsync(() ->
			newsInfoDetailProviderService.getNewsInfoViewCount(newsId)
		);

		NewsInfoViewCount result1 = future1.join();
		NewsInfoViewCount result2 = future2.join();

		// Then
		System.out.printf("스레드1 결과: %d%n", result1.getViewCount());
		System.out.printf("스레드2 결과: %d%n", result2.getViewCount());

		if (result1.getViewCount().equals(result2.getViewCount())) {
			System.out.println("🚨 동시성 문제: 두 스레드가 같은 조회수를 받았습니다!");
			fail("동시성 문제 발생: 조회수 중복");
		} else {
			System.out.println("✅ 정상: 서로 다른 조회수를 받았습니다.");
		}

		// 둘 다 초기값(5)보다 커야 함
		assertThat(result1.getViewCount()).isGreaterThan(5L);
		assertThat(result2.getViewCount()).isGreaterThan(5L);
		assertThat(result1.getViewCount()).isNotEqualTo(result2.getViewCount());
	}
}
