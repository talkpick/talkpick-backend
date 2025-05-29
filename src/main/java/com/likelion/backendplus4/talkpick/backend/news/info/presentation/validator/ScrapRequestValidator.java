package com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.likelion.backendplus4.talkpick.backend.news.info.presentation.dto.ScrapRequest;

@Component
public class ScrapRequestValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ScrapRequest.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ScrapRequest req = (ScrapRequest) target;

		if (!isValidOffset(req)) {
			errors.reject("offset.range",
				"startOffset(" + req.getStartOffset() +
					")는 endOffset(" + req.getEndOffset() + ")보다 작아야 합니다.");
		}
	}

	private boolean isValidOffset(ScrapRequest req) {
		return req.getStartOffset() < req.getEndOffset();
	}
}
