package com.pie.binarytable.config;

import com.pie.binarytable.dao.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	private UserService userService;

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
				.antMatchers("/resources/**", "/", "/index",
						"/registration", "/forgotpassword",
						"/about", "/terms", "/feedback").permitAll()
				.anyRequest().authenticated()
				.and().formLogin().loginPage("/login").defaultSuccessUrl("/goals").permitAll()
				.and().logout().logoutSuccessUrl("/").permitAll();
	}

	//No password encoder during developing
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
	}
}
