package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.entities.Role;
import com.pie.binarytable.entities.User;

import com.pie.binarytable.services.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

/*
Controller for users
 */
@Controller
public class UsersController
{
	@Autowired
	private UserDAO userDAO;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailSender mailSender;

	@GetMapping("/signup")
	public String registration()
	{
		return "signup";
	}

	/*
	Registers the user.
	If registration is successful, redirects to log in,
	else shows error on the registration page.
	*/
	@PostMapping("/signup")
	public String addUser(User user, @RequestParam String repeatPassword, Model model)
	{
		if(!user.getPassword().equals(repeatPassword))
		{
			model.addAttribute("errorMessage", "error.equalPasswords");
			model.addAttribute("nameVal", user.getName());
			model.addAttribute("emailVal", user.getUsername());
			return "signup";
		}
		else
		{
			User userFromDB = userDAO.findByUsername(user.getUsername());

			if(userFromDB != null)
			{
				model.addAttribute("error", "error.emailExists");
				model.addAttribute("nameVal", user.getName());
				model.addAttribute("passwordVal", user.getPassword());
				return "signup";
			}
			if(user.getPassword().length() < 6)
			{
				model.addAttribute("error", "error.shortPassword");
				model.addAttribute("nameVal", user.getName());
				model.addAttribute("emailVal", user.getUsername());
				return "signup";
			}
			if(user.getUsername() == null || user.getUsername().isEmpty())
			{
				model.addAttribute("error", "error.emptyEmail");
				model.addAttribute("nameVal", user.getName());
				model.addAttribute("passwordVal", user.getPassword());
				return "signup";
			}
			if(user.getName() == null || user.getName().isEmpty())
			{
				model.addAttribute("error", "error.emptyName");
				model.addAttribute("emailVal", user.getUsername());
				model.addAttribute("passwordVal", user.getPassword());
				return "signup";
			}

			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setActive(true);
			user.setRoles(Collections.singleton(Role.USER));

			user.setRegistrationDate(LocalDateTime.now().toString());

			userDAO.save(user);

			mailSender.sendGreetingMessage(user.getUsername(), user.getName());
		}

		return "redirect:/login";
	}

	/*
	Get forgotpassword -> Post forgotpassword with user's info -> Get updatepassword to write new password -> Post updatepassword sends new info to db
	*/

	@GetMapping("/forgotpassword")
	public String forgotPass()
	{
		return "forgotpassword";
	}

	@PostMapping("/forgotpassword")
	public String forgotPassword(@RequestParam String email, Model model)
	{
		User user = userDAO.findByUsername(email);
		if(user != null)
		{
			String uuid = UUID.randomUUID().toString();
			user.setUpdatePassword(uuid);
			userDAO.save(user);

			mailSender.sendUpdatePasswordMessage(email, uuid);
		}
		else
		{
			model.addAttribute("error", "error.emailDoesNotExist");
		}

		return "forgotpassword";
	}

	@GetMapping("/updatepassword/{uuid}")
	public String updatePass(Model model, @PathVariable String uuid)
	{
		model.addAttribute("updatePassword", uuid);

		return "updatepassword";
	}

	@PostMapping("/updatepassword")
	public String updatePassword(@RequestParam String email,
	                             @RequestParam String password,
	                             @RequestParam String repeatPassword,
	                             @RequestParam String updatePassword,
	                             Model model)
	{
		String status = "";
		if(!password.equals(repeatPassword))
		{
			status = "error.equalPasswords";
			model.addAttribute("emailVal", email);
		}
		else
		{
			User user = userDAO.findByUsername(email);

			if(user == null)
			{
				status = "error.failToUpdatePassword";
				model.addAttribute("status", status);
				return "updatepassword";
			}

			if(user.getUpdatePassword().equals(updatePassword))
			{
				password = passwordEncoder.encode(password);
				user.setPassword(password);
				userDAO.save(user);
			}

			if(password.equals(userDAO.findByIdEquals(user.getId()).getPassword()))
			{
				status = "status.successUpdatePassword";
				user.setUpdatePassword("");
				userDAO.save(user);
				mailSender.sendNotificationAboutUpdatePasswordMessage(email);
			}
			else
			{
				status = "error.failToUpdatePassword";
			}
		}
		model.addAttribute("status", status);

		return "updatepassword";
	}

	@GetMapping("/feedback")
	public String feedback()
	{
		return "feedback";
	}

	@PostMapping("/feedback")
	public String sendFeedback(@RequestParam String feedback, Model model)
	{
		mailSender.sendFeedbackMessage(feedback);

		model.addAttribute("thanks", "feedback.thankYouForFeedback");

		return "feedback";
	}

}
