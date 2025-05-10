package com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private final String roleName;

}
