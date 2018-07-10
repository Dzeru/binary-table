package com.pie.binarytable.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.mustache.jmustache.LocalizationMessageInterceptor;

import javax.inject.Inject;
import java.util.Locale;

@Configuration
public class LocalizationConfig implements WebMvcConfigurer
{
	@Inject
	private MessageSource messageSource;

	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(localizationMessageInterceptor());
	}
	
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor()
	{
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}
	@Bean
	public LocaleResolver localeResolver()
	{
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("en"));
		return localeResolver;
	}
	
	@Bean
	public LocalizationMessageInterceptor localizationMessageInterceptor()
	{
		LocalizationMessageInterceptor lmi = new LocalizationMessageInterceptor();
		lmi.setLocaleResolver(localeResolver());
		lmi.setMessageSource(messageSource);
		return lmi;
	}
}
