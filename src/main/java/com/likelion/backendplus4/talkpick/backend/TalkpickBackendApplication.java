package com.likelion.backendplus4.talkpick.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TalkpickBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalkpickBackendApplication.class, args);
	}

}
