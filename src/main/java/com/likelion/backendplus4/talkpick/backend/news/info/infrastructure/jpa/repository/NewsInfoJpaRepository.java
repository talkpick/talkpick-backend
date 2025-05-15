package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.ArticleEntity;

/**
 * ArticleEntity에 대한 데이터 접근을 담당하는 JPA 리포지토리 인터페이스입니다.
 *
 * @since 2025-05-14
 */
@Repository
public interface NewsInfoJpaRepository extends JpaRepository<ArticleEntity, Long> {

	boolean existsByLink(String link);

	/**
	 * 주어진 guid로 뉴스 엔티티를 조회합니다.
	 *
	 * @param guid 뉴스 고유 식별자
	 * @return guid에 해당하는 뉴스 엔티티 리스트
	 * @author 함예정
	 * @since 2025-05-14
	 */
	List<ArticleEntity> findByGuid(String guid);
}