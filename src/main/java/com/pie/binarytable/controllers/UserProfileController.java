package com.pie.binarytable.controllers;

import com.pie.binarytable.repositories.UserRepository;
import com.pie.binarytable.entities.User;
import com.pie.binarytable.services.MailSender;

import com.pie.binarytable.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

/*
Controller for user profile, where user can change email and password
 */
@Controller
public class UserProfileController
{
	@Autowired
	private UserRepository userDAO;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailSender mailSender;

	@GetMapping("/profile")
	public String profile(Principal principal,
	                      Model model)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());

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

		return "profile";
	}

	@PostMapping("/updatepasswordprofile")
	public String updatePasswordProfile(Principal principal,
	                                    @RequestParam String oldPassword,
	                                    @RequestParam String newPassword,
	                                    @RequestParam String repeatPassword,
	                                    Model model)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());

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

		return "profile";
	}

	@PostMapping("/updateemailprofile")
	public String updateEmailProfile(Principal principal,
	                                 @RequestParam String oldEmail,
	                                 @RequestParam String newEmail,
	                                 @RequestParam String repeatEmail,
	                                 @RequestParam String password,
	                                 Model model)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());

		if(!passwordEncoder.matches(password, user.getPassword()))
		{
			model.addAttribute("emailStatus", "error.equalPasswords");
		}
		else
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

		return "profile";
	}
}
