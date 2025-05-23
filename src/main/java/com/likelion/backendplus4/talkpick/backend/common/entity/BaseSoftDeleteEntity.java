package com.likelion.backendplus4.talkpick.backend.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BaseSoftDeleteEntity extends BaseAuditableEntity{

	private boolean isDelete;

	public void markAsDeleted() {
		this.isDelete = true;
	}

}
