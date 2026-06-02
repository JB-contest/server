package com.example.jb.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("JB Compliance Review API")
						.description("대출 문서 컴플라이언스 검토 시스템 API")
						.version("v1.0.0"));
	}
}
