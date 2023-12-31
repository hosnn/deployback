package com.example.sbre.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:3000")
        	.allowedOrigins("https://testf-5ba01.web.app")
            .allowedMethods("GET", "POST", "PUT", "DELETE");
//            .allowedHeaders("Content-Type", "Authorization")
	}
	
}
