package com.likelion.backendplus4.talkpick.backend.news.info.application.port.out;

public interface PopularNewsPort {
	String getTop1NewsId(String category);

	String getTop1NewsWithScore(String category);

	void saveRankingHash(String category, String hashValue);

	String getSavedRankingHash(String category);
}