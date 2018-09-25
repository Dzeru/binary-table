package com.pie.binarytable.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.*;

@Service
public class MailSender
{
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
	private String username;

	private String begin = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>" +
			"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />" +
			"<meta name=\"format-detection\" content=\"telephone=no\">" +
			"<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"></head><body><table width=\"70%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
			"<tr><td>";
	private String end = "</td></tr></table></body></html>";

	private String greeting = "<p>Hi, %s</p>";

	private String it = "<p>It's Binary Table. ";

	private String sign = "<br>We wish you reachable goals!<br>Pie Team";

	private String aware = "<p>If you don't figure out what this email is about, ignore it. " +
			               "If it isn't you who wants to update password," +
			               "ignore it too or contact us to prevent account theft.</p>";

	private String greetingEmail = "<p>Thanks for signing up to Binary Table! " +
			                       "Now you can track your progress smarter. " +
			                       "You can log in <a href=\"http://binarytable.herokuapp.com/login\">here</a> with the email address %s .</p>" +
			                       "<p>We are open for dialog, so you can reply to this email or send " +
			                       "a new one to our address to get in touch with us directly. " +
			                       "You can find all our contacts <a href=\"http://binarytable.herokuapp.com/contacts\">here</a>. All sorts of feedback are welcomed!</p>";

	private String forgotPassword = "If you forgot password, visit next link to update it: " +
			                        "http://binarytable.herokuapp.com/updatepassword/%s .</p>";

	private String passwordWasUpdated = "Your password was successfully updated.</p>";

	public void send(String emailTo, String subject, String message)
	{
		try
		{
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

			helper.setTo(emailTo);
			helper.setFrom(username);
			mimeMessage.setSubject(subject, "utf-8");
			mimeMessage.setContent(message, "text/html;charset=utf-8");

			mailSender.send(mimeMessage);
		}
		catch(MessagingException e)
		{
			e.printStackTrace();
		}
	}
	
	public void sendGreetingMessage(String emailTo, String name)
	{
		String message = begin + String.format(greeting, name) +
				         String.format(greetingEmail, emailTo) + sign + end;
		send(emailTo, "Welcome to Binary Table", message);
	}

	public void sendUpdatePasswordMessage(String emailTo, String uuid)
	{
		String message = begin + it + String.format(forgotPassword, uuid) + aware + sign + end;
		send(emailTo, "Update password", message);
	}

	public void sendNotificationAboutUpdatePasswordMessage(String emailTo)
	{
		String message = begin + it + passwordWasUpdated + aware + sign + end;
		send(emailTo, "Your password was updated", message);
	}

	public void sendFeedbackMessage(String feedback)
	{
		send(username, "Feedback", feedback);
	}
}
