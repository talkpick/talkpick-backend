package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 동시성 문제와 성능을 종합적으로 비교하는 테스트
 */
@DisplayName("동시성 및 성능 종합 비교 테스트")
class ConcurrencySolutionTest {

	static class ViewCountSimulator {
		private AtomicLong currentCount = new AtomicLong(10);
		private final Object lock = new Object();

		// 현재 구조와 같은 문제가 있는 메서드
		public Long getViewCountWithProblem() {
			Long current = currentCount.get();

			try {
				Thread.sleep(1); // 동시성 문제 재현용 딜레이
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

			if (shouldIncrease()) {
				Long increased = current + 1;
				currentCount.set(increased);
				return increased;
			}
			return current;
		}

		// Lock으로 해결한 버전
		public Long getViewCountWithLock() {
			synchronized(lock) {
				Long current = currentCount.get();

				try {
					Thread.sleep(1); // 실제 Redis 지연 시뮬레이션
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

				if (shouldIncrease()) {
					Long increased = current + 1;
					currentCount.set(increased);
					return increased;
				}
				return current;
			}
		}

		// 원자적 연산으로 해결한 버전
		public Long getViewCountFixed() {
			if (shouldIncrease()) {
				return currentCount.incrementAndGet();
			}
			return currentCount.get();
		}

		private boolean shouldIncrease() {
			return true;
		}

		public void reset() {
			currentCount.set(10);
		}
	}

	@Test
	@DisplayName("동시성 문제 해결방안 종합 비교")
	void testConcurrencySolutionsComparison() {
		System.out.println("\n" + "=".repeat(80));
		System.out.println("🔥 동시성 문제 해결방안 종합 비교 테스트 시작");
		System.out.println("=".repeat(80));

		long overallStartTime = System.currentTimeMillis();
		int threadCount = 50;

		System.out.println("\n🚨 1단계: 현재 구조 (문제 있는 코드) 테스트");
		System.out.println("-".repeat(60));

		ViewCountSimulator simulator1 = new ViewCountSimulator();
		long startTime1 = System.currentTimeMillis();

		List<CompletableFuture<Long>> futures1 = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			futures1.add(CompletableFuture.supplyAsync(() -> simulator1.getViewCountWithProblem()));
		}
		CompletableFuture.allOf(futures1.toArray(new CompletableFuture[0])).join();

		long problemTime = System.currentTimeMillis() - startTime1;
		long final1 = simulator1.currentCount.get();
		long missed1 = (10 + threadCount) - final1;

		System.out.printf("⏱️  실행시간: %dms%n", problemTime);
		System.out.printf("📊 최종 조회수: %d (예상: %d, 누락: %d개)%n", final1, 10 + threadCount, missed1);
		System.out.printf("🎯 정확성: %.1f%% (%d/%d)%n",
			(double)(final1 - 10) / threadCount * 100, final1 - 10, threadCount);

		System.out.println("\n🔒 2단계: Lock 해결책 테스트");
		System.out.println("-".repeat(60));

		ViewCountSimulator simulator2 = new ViewCountSimulator();
		simulator2.reset();
		long startTime2 = System.currentTimeMillis();

		List<CompletableFuture<Long>> futures2 = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			futures2.add(CompletableFuture.supplyAsync(() -> simulator2.getViewCountWithLock()));
		}
		CompletableFuture.allOf(futures2.toArray(new CompletableFuture[0])).join();

		long lockTime = System.currentTimeMillis() - startTime2;
		long final2 = simulator2.currentCount.get();

		System.out.printf("⏱️  실행시간: %dms (%.1fx 느림)%n", lockTime, (double)lockTime / problemTime);
		System.out.printf("📊 최종 조회수: %d (예상: %d)%n", final2, 10 + threadCount);
		System.out.printf("🎯 정확성: %.1f%% (%d/%d)%n",
			(double)(final2 - 10) / threadCount * 100, final2 - 10, threadCount);

		System.out.println("\n⚡ 3단계: 원자적 연산 (Redis INCR) 해결책 테스트");
		System.out.println("-".repeat(60));

		ViewCountSimulator simulator3 = new ViewCountSimulator();
		simulator3.reset();
		long startTime3 = System.currentTimeMillis();

		List<CompletableFuture<Long>> futures3 = new ArrayList<>();
		for (int i = 0; i < threadCount; i++) {
			futures3.add(CompletableFuture.supplyAsync(() -> simulator3.getViewCountFixed()));
		}
		CompletableFuture.allOf(futures3.toArray(new CompletableFuture[0])).join();

		long atomicTime = System.currentTimeMillis() - startTime3;
		long final3 = simulator3.currentCount.get();

		System.out.printf("⏱️  실행시간: %dms (%.1fx 속도)%n", atomicTime, (double)atomicTime / problemTime);
		System.out.printf("📊 최종 조회수: %d (예상: %d)%n", final3, 10 + threadCount);
		System.out.printf("🎯 정확성: %.1f%% (%d/%d)%n",
			(double)(final3 - 10) / threadCount * 100, final3 - 10, threadCount);

		System.out.println("\n" + "=".repeat(80));
		System.out.println("📊 최종 성능 & 정확성 비교 결과");
		System.out.println("=".repeat(80));

		System.out.printf("%-20s | %8s | %8s | %8s | %s%n",
			"해결방안", "시간(ms)", "배속", "정확성", "상태");
		System.out.println("-".repeat(80));

		System.out.printf("%-20s | %8dms | %8s | %7.1f%% | %s%n",
			"🚨 현재 구조", problemTime, "1.0x", (double)(final1 - 10) / threadCount * 100,
			final1 == 10 + threadCount ? "✅" : "❌ 조회수 누락");

		System.out.printf("%-20s | %8dms | %8.1fx | %7.1f%% | %s%n",
			"🔒 Lock 해결책", lockTime, (double)lockTime / problemTime, (double)(final2 - 10) / threadCount * 100,
			final2 == 10 + threadCount ? "✅ 정확하지만 느림" : "❌");

		System.out.printf("%-20s | %8dms | %8.1fx | %7.1f%% | %s%n",
			"⚡ Redis INCR", atomicTime, (double)atomicTime / problemTime, (double)(final3 - 10) / threadCount * 100,
			final3 == 10 + threadCount ? "✅ 빠르고 정확함" : "❌");


		assertThat(final2).isEqualTo(10 + threadCount);
		assertThat(final3).isEqualTo(10 + threadCount);
		assertThat(final1).isLessThan(10 + threadCount);
	}
}