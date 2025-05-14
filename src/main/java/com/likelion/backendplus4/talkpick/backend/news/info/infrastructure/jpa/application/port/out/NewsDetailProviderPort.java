package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.application.port.out;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;

public interface NewsDetailProviderPort {
	NewsInfoDetail getNewsInfoDetailsByArticleId(String guid);
}
