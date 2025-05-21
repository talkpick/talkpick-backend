package com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator;

import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.config.NewsViewCountProperties;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NewsIdValidator implements ConstraintValidator<NewsIdConstraint, String> {

    private final NewsViewCountProperties newsViewCountProperties;
    private String[] validPrefixes;

    @Override
    public void initialize(NewsIdConstraint constraintAnnotation) {
        String validPrefixesStr = newsViewCountProperties.getValidPrefixes();
        this.validPrefixes = validPrefixesStr != null ? validPrefixesStr.split(",") : new String[0];
    }

    @Override
    public boolean isValid(String newsId, ConstraintValidatorContext context) {
        if (newsId == null || newsId.isEmpty()) {
            return false;
        }

        for (String prefix : validPrefixes) {
            if (newsId.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }
}