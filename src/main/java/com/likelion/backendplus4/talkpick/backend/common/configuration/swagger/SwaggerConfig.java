package com.likelion.backendplus4.talkpick.backend.common.configuration.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	private static final String SECURITY_SCHEME_NAME = "BearerAuth";


	@Bean
	public OpenAPI securityOpenAPI() { // 보안 관련 설정
		return new OpenAPI()
			.info(new Info()
				.title("TalkPick API")
				.description("TalkPick 프로젝트의 API 문서")
				.version("1.0.0"))
			.components(new io.swagger.v3.oas.models.Components()
				.addSecuritySchemes(SECURITY_SCHEME_NAME,
					new SecurityScheme()
						.name(SECURITY_SCHEME_NAME)
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")));
	}
}
