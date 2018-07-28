package com.pie.binarytable.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;

@Service
public class MailSender
{
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
	private String username;
	
	public void send(String emailTo, String subject, String message)
	{
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		
		mailMessage.setFrom(username);
		mailMessage.setTo(emailTo);
		mailMessage.setSubject(subject);
		mailMessage.setText(message);
		
		mailSender.send(mailMessage);
	}
	
	public void sendGreetingMessage(String emailTo)
	{
		String message = "Welcome to Binary Table! We wish you reachable goals!";
		send(emailTo, "Welcome to Binary Table", message);
	}

	public void sendUpdatePasswordMessage(String emailTo, String uuid)
	{
		String message = String.format("If you forgot password, visit next link to update it: http://localhost:8080/updatepassword/%s", uuid);
		send(emailTo, "Update password", message);
	}

	public void sendNotificationAboutUpdatePasswordMessage(String emailTo)
	{
		String message = "Your password was updated. If it was not you who did it, mail us!";
		send(emailTo, "Your password was updated", message);
	}

	public void sendFeedbackMessage(String feedback)
	{
		send(username, "Feedback", feedback);
	}
}

