package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(this.getPasswordEncoder());
		
		return daoAuthenticationProvider;
	}
	
	/*
	 * protected void configure(AuthenticationManagerBuilder auth) throws Exception{
	 * auth.authenticationProvider(daoAuthenticationProvider()); }
	 */
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http.authorizeHttpRequests(requests ->{requests.requestMatchers("/admin/**").hasRole("ADMIN");
		requests.requestMatchers("/user/**").hasRole("USER");
		requests.requestMatchers("/**").permitAll();});
		/*
		 * 
		 * .and(). formLogin().and().csrf(csrf ->
		 * );
		 */
		http.formLogin(login -> {login.defaultSuccessUrl("/user/index", true)
				.loginPage("/sign-in");
			/* login.loginProcessingUrl("/authentication"); */});
		
		http.csrf(csrf -> csrf.disable());
		return http.build();
	}
}
