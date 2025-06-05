package com.likelion.backendplus4.talkpick.backend.chat.domain.model;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum CategoryType {
    POLITICS("politics"),
    SOCIETY("society"),
    ECONOMY("economy"),
    SPORTS("sports"),
    INTERNATIONAL("international"),
    ENTERTAINMENT("entertainment");

    private final String value;

    CategoryType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Set<String> getValidValues() {
        return Arrays.stream(values())
                .map(CategoryType::getValue)
                .collect(Collectors.toSet());
    }

    public static boolean isValid(String value) {
        if (value == null) return false;
        return Arrays.stream(values())
                .anyMatch(type -> type.getValue().equalsIgnoreCase(value));
    }
}