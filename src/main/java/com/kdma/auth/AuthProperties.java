package com.kdma.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties("auth")
public class AuthProperties {
	private String redirectionUrl;
	private String emailFrom;
	private String corsAllowedOrigins;
}
