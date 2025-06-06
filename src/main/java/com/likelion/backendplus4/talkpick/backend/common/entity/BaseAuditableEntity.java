package com.likelion.backendplus4.talkpick.backend.common.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseAuditableEntity {

	@CreatedDate
	@Column(name = "created_time")
	private LocalDateTime createdTime;

	@LastModifiedDate
	@Column(name = "updated_time")
	private LocalDateTime updatedTime;

}
