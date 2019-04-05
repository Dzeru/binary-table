package com.pie.binarytable.config;

import com.pie.binarytable.services.AuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	private AuthProvider authProvider;

	@Bean
	PasswordEncoder passwordEncoder()
	{
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http
				.authorizeRequests()
				.antMatchers("/resources/**", "/", "/index", "/login**",
						"/signup", "/forgotpassword", "/updatepassword/**", "/detect-device",
						"/about", "/terms", "/feedback", "/contacts", "/usecases", "/error").permitAll()
				.anyRequest().authenticated()
				.and().formLogin().loginPage("/login").defaultSuccessUrl("/goals").failureUrl("/login?error").permitAll()
				.and().logout().logoutSuccessUrl("/").permitAll();
	}

	@Override
	public void configure(WebSecurity security)
	{
		security.ignoring().antMatchers("/css/**", "/js/**", "/images/**", "/favicon.png");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
	{
		auth.authenticationProvider(authProvider);
	}
}
