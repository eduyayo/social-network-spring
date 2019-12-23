package com.pigdroid.spring.social.config;

import static com.pigdroid.spring.social.config.Constants.AVATAR_FOLDER;
import static com.pigdroid.spring.social.config.Constants.REMEMBER_ME_COOKIE;
import static com.pigdroid.spring.social.config.Constants.REMEMBER_ME_TOKEN;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.pigdroid.spring.social.security.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/**/*.js")
                .antMatchers("/**/*.ico")
                .antMatchers("/**/*.css")
                .antMatchers("/**/*.otf")
                .antMatchers("/**/*.eot")
                .antMatchers("/**/*.svg")
                .antMatchers("/**/*.ttf")
                .antMatchers("/**/*.woff")
                .antMatchers("/**/*.woff2")
                .antMatchers("/**/*.html")
                .antMatchers("/bootstrap/**")
                .antMatchers("/" + AVATAR_FOLDER + "undefined.gif")
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        final String[] swagger = {
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/v2/api-docs",
                "/webjars/**"
        };

		// @formatter:off
        http
            .cors()
                .and()
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
            .httpBasic()
                .and()
			.authorizeRequests()
				.antMatchers(swagger).permitAll()
				.antMatchers("/").permitAll()
				.antMatchers("/console/**").permitAll() // TODO: Enables h2 console - only for development environment
				.antMatchers(HttpMethod.GET,"/api/login").permitAll()
				.antMatchers(HttpMethod.POST,"/api/signUp").permitAll()
				.antMatchers("/signin/**").permitAll()
                .antMatchers("/api/**").authenticated()
				.and()
			.logout()
				.logoutUrl("/api/logout")
                .deleteCookies(REMEMBER_ME_COOKIE)
				.permitAll()
				.and()
            .headers()
				.frameOptions()
				.disable()
				.and()
			.rememberMe()
				.rememberMeServices(rememberMeService())
				.key(Constants.REMEMBER_ME_TOKEN)
				.and()
			.exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
		;
		// @formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenBasedRememberMeServices rememberMeService() {
        final TokenBasedRememberMeServices services =
                new TokenBasedRememberMeServices(REMEMBER_ME_TOKEN, this.userDetailsService);

        services.setCookieName(REMEMBER_ME_COOKIE);
        services.setTokenValiditySeconds(3600);
        services.setAlwaysRemember(true);

        return services;
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setMaxAge(1800L);
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}