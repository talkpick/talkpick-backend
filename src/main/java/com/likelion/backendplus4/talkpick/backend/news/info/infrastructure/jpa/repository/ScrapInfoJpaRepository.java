package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.ScrapEntity;

public interface ScrapInfoJpaRepository extends JpaRepository<ScrapEntity, Long> {
	List<ScrapEntity> findAllByNewsId(String newsId);
}
