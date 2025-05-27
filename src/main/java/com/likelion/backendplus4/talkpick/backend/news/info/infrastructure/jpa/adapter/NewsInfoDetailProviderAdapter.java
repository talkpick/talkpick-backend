package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoDetailResponseMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.PopularNewsResponseMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.ArticleEntity;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.mapper.ArticleEntityMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;

import lombok.RequiredArgsConstructor;

/**
 * NewsDetailProviderPort 인터페이스의 구현체로,
 * JPA를 통해 뉴스 상세 정보를 조회하고 DTO 변환을 담당하는 어댑터 클래스입니다.
 *
 * @since 2025-05-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsInfoDetailProviderAdapter implements NewsDetailProviderPort {
    private final NewsInfoJpaRepository newsInfoJpaRepository;

    /**
     * 주어진 guid(뉴스 ID)를 기준으로 뉴스 상세 정보를 조회합니다.
     * 조회된 뉴스가 정확히 하나인 경우에만 도메인 객체로 변환하여 반환합니다.
     *
     * @param guid 뉴스의 고유 식별자
     * @return 뉴스 상세 도메인 객체
     * @throws NewsInfoException 뉴스 조회 실패 또는 중복 발견 시
     * @author 함예정
     * @since 2025-05-14
     */
    @Override
    public NewsInfoDetail getNewsInfoDetailsByArticleId(String guid) {
        ArticleEntity entity = getArticleEntityByGuid(guid);
        return ArticleEntityMapper.toDetailFromEntity(entity);
    }

    /**
     * 주어진 guid(뉴스 ID)를 기준으로 인기뉴스 응답 형태로 조회합니다.
     *
     * 1. 데이터베이스에서 뉴스 엔티티 조회
     * 2. 엔티티를 도메인 모델로 변환
     * 3. 도메인 모델을 인기뉴스 DTO로 변환
     *
     * @param guid 뉴스의 고유 식별자
     * @return 인기뉴스 응답 DTO
     * @throws NewsInfoException 뉴스 조회 실패 또는 변환 실패 시
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public PopularNewsResponse getPopularNewsByArticleId(String guid) {
        return convertToPopularNewsResponse(guid);
    }

    /**
     * 뉴스 ID와 조회수를 함께 받아 NewsInfoDetailResponse로 반환합니다.
     *
     * 1. 데이터베이스에서 뉴스 엔티티 조회
     * 2. 엔티티를 도메인 모델로 변환
     * 3. 도메인 모델과 현재 조회수를 뉴스 상세 DTO로 변환
     *
     * @param newsId    뉴스의 고유 식별자
     * @param viewCount 현재 조회수
     * @return 뉴스 상세 응답 DTO (조회수 포함)
     * @throws NewsInfoException 뉴스 조회 실패 또는 변환 실패 시
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public NewsInfoDetailResponse getNewsInfoDetailResponseWithViewCount(String newsId, Long viewCount) {
        return convertToNewsDetailResponseWithViewCount(newsId, viewCount);
    }

    /**
     * 뉴스를 인기뉴스 응답 DTO로 변환합니다.
     *
     * @param guid 뉴스의 고유 식별자
     * @return 인기뉴스 응답 DTO
     * @throws NewsInfoException 변환 실패 시
     */
    private PopularNewsResponse convertToPopularNewsResponse(String guid) {
        try {
            ArticleEntity entity = getArticleEntityByGuid(guid);
            NewsInfoDetail domain = ArticleEntityMapper.toDetailFromEntity(entity);
            return PopularNewsResponseMapper.toResponse(domain);
        } catch (NewsInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.NEWS_INFO_NOT_FOUND, e);
        }
    }

    /**
     * 뉴스를 상세 응답 DTO로 변환합니다.
     *
     * @param newsId    뉴스의 고유 식별자
     * @param viewCount 현재 조회수
     * @return 뉴스 상세 응답 DTO
     * @throws NewsInfoException 변환 실패 시
     */
    private NewsInfoDetailResponse convertToNewsDetailResponseWithViewCount(String newsId, Long viewCount) {
        try {
            ArticleEntity entity = getArticleEntityByGuid(newsId);
            NewsInfoDetail domain = ArticleEntityMapper.toDetailFromEntity(entity);
            return NewsInfoDetailResponseMapper.toResponseWithViewCount(domain, viewCount);
        } catch (NewsInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.NEWS_INFO_NOT_FOUND, e);
        }
    }

    /**
     * GUID로 뉴스 엔티티를 조회하고 유효성을 검증합니다.
     *
     * @param guid 뉴스의 고유 식별자
     * @return 단일 뉴스 엔티티
     * @throws NewsInfoException 뉴스가 없거나 중복인 경우
     */
    private ArticleEntity getArticleEntityByGuid(String guid) {
        List<ArticleEntity> savedNewsInfo = newsInfoJpaRepository.findByGuid(guid);
        return getOnlyArticleOrThrow(savedNewsInfo);
    }

    /**
     * 조회된 뉴스가 정확히 하나인지 검증하고, 단일 엔티티를 반환합니다.
     *
     * @param savedNewsInfo guid 기준으로 조회된 뉴스 리스트
     * @return 단일 뉴스 엔티티
     * @throws NewsInfoException 조회된 뉴스가 1건이 아닌 경우
     * @author 함예정
     * @since 2025-05-14
     */
    private ArticleEntity getOnlyArticleOrThrow(List<ArticleEntity> savedNewsInfo) {
        if (savedNewsInfo.size() == 1) {
            return savedNewsInfo.getFirst();
        }
        throw new NewsInfoException(NewsInfoErrorCode.NON_UNIQUE_NEWS_INFO);
    }
}