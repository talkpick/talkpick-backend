package com.likelion.backendplus4.talkpick.backend.sample.common.logging;

import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.LogMethodValues;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.TimeTracker;

@Service
public class TestLoggingService {
	@EntryExitLog
	@LogMethodValues
	@TimeTracker
	public String test(String text) {
		return "bye";
	}
}
