package com.likelion.backendplus4.talkpick.backend.sample.common.logging;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.LogJson;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/testController")
@RequiredArgsConstructor
public class TestLoggingController {
	private final TestLoggingService testLoggingService;

	@LogJson
	@PostMapping
	public String test(@RequestBody TestLoggingRequest testLoggingRequest) {
		return testLoggingService.test(testLoggingRequest.getName());
	}
}
