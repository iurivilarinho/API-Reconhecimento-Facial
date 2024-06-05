package com.br.face.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://198.27.114.51:4000", "http://198.27.114.51:5395", "http://localhost:5173",
						"https://reconhecimento-facial-neves.vercel.app/",
						"https://saladetestes.com.br/")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
				.allowCredentials(true) // Permitir credenciais (cookies)
				.allowedHeaders("*"); // Permitir todos os cabeçalhos
		;
	}
}