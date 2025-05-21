package com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NewsIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NewsIdConstraint {
    String message() default "뉴스 ID는 유효한 형식이어야 합니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}