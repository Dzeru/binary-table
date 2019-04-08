package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.entities.User;
import com.pie.binarytable.services.MailSender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

/*
Controller for user profile, where user can change email and password
 */
@Controller
public class UserProfileController
{
	@Autowired
	private UserDAO userDAO;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailSender mailSender;

	@GetMapping("/profile")
	public String profile(@AuthenticationPrincipal User user, Model model, Device device)
	{
		model.addAttribute("user", user);
		model.addAttribute("name", user.getName());
		model.addAttribute("email", user.getUsername());

		/*
		from yyyy-mm-ddThh:mi:se.ms
		 */
		String regDate = user.getRegistrationDate();
		String year = regDate.substring(0, 4);
		String month = regDate.substring(5, 7);
		String day = regDate.substring(8, 10);
		String time = regDate.substring(11, 16);

		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("day", day);
		model.addAttribute("time", time);

		if(device.isNormal())
		{
			return "profile";
		}
		else
		{
			return "profilecompact";
		}
	}

	@PostMapping("/updatepasswordprofile")
	public String updatePasswordProfile(@AuthenticationPrincipal User user,
	                                    @RequestParam String oldPassword,
	                                    @RequestParam String newPassword,
	                                    @RequestParam String repeatPassword,
	                                    Model model, Device device)
	{
		if(!passwordEncoder.matches(oldPassword, user.getPassword()) || !newPassword.equals(repeatPassword))
		{
			model.addAttribute("passwordStatus", "error.equalPasswords");
		}
		else
		{
			newPassword = passwordEncoder.encode(newPassword);
			user.setPassword(newPassword);
			userDAO.save(user);

			if(passwordEncoder.matches(repeatPassword, user.getPassword()))
			{
				model.addAttribute("passwordStatus", "status.successUpdatePassword");
				mailSender.sendNotificationAboutUpdatePasswordMessage(user.getUsername());
			}
		}

		model.addAttribute("user", user);
		model.addAttribute("name", user.getName());
		model.addAttribute("email", user.getUsername());

		/*
		from yyyy-mm-ddThh:mi:se.ms
		 */
		String regDate = user.getRegistrationDate();
		String year = regDate.substring(0, 4);
		String month = regDate.substring(5, 7);
		String day = regDate.substring(8, 10);
		String time = regDate.substring(11, 16);

		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("day", day);
		model.addAttribute("time", time);

		if(device.isNormal())
		{
			return "profile";
		}
		else
		{
			return "profilecompact";
		}
	}

	@PostMapping("/updateemailprofile")
	public String updateEmailProfile(@AuthenticationPrincipal User user,
	                                 @RequestParam String oldEmail,
	                                 @RequestParam String newEmail,
	                                 @RequestParam String repeatEmail,
	                                 Model model, Device device)
	{
			if(newEmail.equals(repeatEmail) && userDAO.findByUsername(oldEmail) != null)
			{
				user.setUsername(newEmail);
				userDAO.save(user);
			}

			if(userDAO.findByUsername(newEmail) != null)
			{
				model.addAttribute("emailStatus", "status.successUpdateEmail");
			}

		model.addAttribute("user", user);
		model.addAttribute("name", user.getName());
		model.addAttribute("email", user.getUsername());

		/*
		from yyyy-mm-ddThh:mi:se.ms
		 */
		String regDate = user.getRegistrationDate();
		String year = regDate.substring(0, 4);
		String month = regDate.substring(5, 7);
		String day = regDate.substring(8, 10);
		String time = regDate.substring(11, 16);

		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("day", day);
		model.addAttribute("time", time);

		if(device.isNormal())
		{
			return "profile";
		}
		else
		{
			return "profilecompact";
		}
	}
}
