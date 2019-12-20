package com.pigdroid.spring.social.config;

import static com.pigdroid.spring.social.config.Constants.AVATAR_FOLDER;

import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

	@Bean // TODO: Enables h2 console - only for development environment
	ServletRegistrationBean<WebServlet> h2servletRegistration() {
		ServletRegistrationBean<WebServlet> registrationBean = new ServletRegistrationBean<WebServlet>(new WebServlet());
		registrationBean.addUrlMappings("/console/*");
		return registrationBean;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(AVATAR_FOLDER + "**").addResourceLocations(AVATAR_FOLDER);

		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//		registry.addResourceHandler("/webjars/**")
//				.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		ServletInvocableHandlerMethod d;
		configurer.favorPathExtension(true);
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseSuffixPatternMatch(true);
	}

}
