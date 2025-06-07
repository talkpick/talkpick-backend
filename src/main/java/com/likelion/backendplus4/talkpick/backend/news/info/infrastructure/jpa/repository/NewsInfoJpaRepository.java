package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	@Query("""
      select distinct a
      from ArticleEntity a
      left join fetch a.scrapEntities
      where a.guid = :guid
    """)
	Optional<ArticleEntity> findByGuidWithScraps(@Param("guid") String guid);

	@Query("""
    select a
    from ArticleEntity a
    join fetch a.scrapEntities s
    where s.userId = :userId
""")
	List<ArticleEntity> findScrappedArticleByUserId(@Param("userId") Long userId);

	/**
	 * 전체 뉴스 목록을 ID 내림차순으로 페이지네이션하여 조회합니다.
	 *
	 * @param pageable 페이지 정보
	 * @return 뉴스 엔티티 슬라이스
	 * @author 함예정
	 * @since 2025-05-26
	 */
	Slice<ArticleEntity> findAllByOrderByIdDesc(Pageable pageable);

	/**
	 * 특정 카테고리의 뉴스 목록을 ID 내림차순으로 페이지네이션하여 조회합니다.
	 *
	 * @param category 뉴스 카테고리
	 * @param pageable 페이지 정보
	 * @return 뉴스 엔티티 슬라이스
	 * @author 함예정
	 * @since 2025-05-26
	 */
	Slice<ArticleEntity> findAllByCategoryOrderByIdDesc(String category, Pageable pageable);

	/**
	 * 지정된 ID보다 작은 ID를 가진 뉴스 목록을 ID 내림차순으로 조회합니다.
	 *
	 * @param id 기준이 되는 ID (미포함)
	 * @param pageable 페이지 정보
	 * @return 뉴스 엔티티 슬라이스
	 * @author 함예정
	 * @since 2025-05-26
	 */
	Slice<ArticleEntity> findAllByIdLessThanOrderByIdDesc(long id, Pageable pageable);

	/**
	 * 특정 카테고리 내에서 지정된 ID보다 작은 ID를 가진 뉴스 목록을 ID 내림차순으로 조회합니다.
	 *
	 * @param category 뉴스 카테고리
	 * @param id 기준이 되는 ID (미포함)
	 * @param pageable 페이지 정보
	 * @return 뉴스 엔티티 슬라이스
	 * @author 함예정
	 * @since 2025-05-26
	 */
	Slice<ArticleEntity> findAllByCategoryAndIdLessThanOrderByIdDesc(String category, Long id, Pageable pageable);
}