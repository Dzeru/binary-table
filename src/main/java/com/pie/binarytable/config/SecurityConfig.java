package com.pie.binarytable.config;

import com.pie.binarytable.dao.UserAccountsDAO;
import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.entities.Role;
import com.pie.binarytable.entities.User;
import com.pie.binarytable.entities.UserAccounts;
import com.pie.binarytable.services.AuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.Filter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Configuration
@EnableOAuth2Client
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	private AuthProvider authProvider;

	@Autowired
	private OAuth2ClientContext oAuth2ClientContext;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private UserAccountsDAO userAccountsDAO;

	@Bean
	PasswordEncoder passwordEncoder()
	{
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder;
	}

	@Bean
	public FilterRegistrationBean oAuth2ClientFilterRegistration(OAuth2ClientContextFilter oAuth2ClientContextFilter)
	{
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(oAuth2ClientContextFilter);
		registration.setOrder(-100);
		return registration;
	}

	private Filter ssoFilter()
	{
		OAuth2ClientAuthenticationProcessingFilter googleFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/google");
		OAuth2RestTemplate googleTemplate = new OAuth2RestTemplate(google(), oAuth2ClientContext);
		googleFilter.setRestTemplate(googleTemplate);
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(googleResource().getUserInfoUri(), google().getClientId());
		tokenServices.setRestTemplate(googleTemplate);
		googleFilter.setTokenServices(tokenServices);
		return googleFilter;
	}

	/*@Bean
	public PrincipalExtractor principalExtractor(UserDAO userDAO)
	{
		return map ->
		{
			Long id = (Long) map.get("sub");

			User user = userDAO.findByUserAccountsId(id);

			if(user == null)
			{
				User newUser = new User();

				newUser.setName((String) map.get("name"));
				newUser.setUsername((String) map.get("email"));
				newUser.setRegistrationDate(LocalDateTime.now().toString());
				newUser.setPassword(passwordEncoder.encode(user.getName() + user.getUsername()));
				newUser.setActive(true);
				newUser.setRoles(Collections.singleton(Role.USER));

				UserAccounts userAccounts = new UserAccounts();
				userAccounts.setId(id);
				userAccounts.setBinaryTableName(newUser.getName());
				userAccounts.setBinaryTableUsername(newUser.getUsername());
				userAccounts.setGoogleUsername(newUser.getUsername());

				userAccountsDAO.save(userAccounts);
				userAccounts = userAccountsDAO.findByBinaryTableUsername(newUser.getUsername());
				newUser.setUserAccountsId(userAccounts.getId());
			}
			return userDAO.save(user);
		};
	}*/

	@Bean
	@ConfigurationProperties("google.client")
	public AuthorizationCodeResourceDetails google()
	{
		return new AuthorizationCodeResourceDetails();
	}

	@Bean
	@ConfigurationProperties("google.resource")
	public ResourceServerProperties googleResource()
	{
		return new ResourceServerProperties();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http
				.authorizeRequests()
				.antMatchers("/resources/**", "/", "/index", "/login**",
						"/signup", "/forgotpassword", "/updatepassword/**",
						"/about", "/terms", "/feedback", "/contacts", "/usecases", "/error").permitAll()
				.anyRequest().authenticated()
				.and().formLogin().loginPage("/login").defaultSuccessUrl("/goals").failureUrl("/login?error").permitAll()
				.and().logout().logoutSuccessUrl("/").permitAll()
				.and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
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
