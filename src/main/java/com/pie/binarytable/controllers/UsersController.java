package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.entities.Role;
import com.pie.binarytable.entities.User;

import com.pie.binarytable.services.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

	@GetMapping("/registration")
	public String registration()
	{
		return "registration";
	}

	/*
	Registers the user.
	If registration is successful, redirects to log in,
	else shows error on the registration page.
	*/
	@PostMapping("/registration")
	public String addUser(User user, @RequestParam String repeatPassword, Model model)
	{
		if(!user.getPassword().equals(repeatPassword))
		{
			model.addAttribute("errorMessage", "error.equalPasswords");
			model.addAttribute("nameVal", user.getName());
			model.addAttribute("emailVal", user.getUsername());
			return "registration";
		}
		else
		{
			User userFromDB = userDAO.findByUsername(user.getUsername());

			if(userFromDB != null)
			{
				model.addAttribute("errorMessage", "error.emailExists");
				model.addAttribute("nameVal", user.getName());
				model.addAttribute("passwordVal", user.getPassword());
				return "registration";
			}
			if(user.getPassword().length() < 6)
			{
				model.addAttribute("errorMessage", "error.shortPassword");
				model.addAttribute("nameVal", user.getName());
				model.addAttribute("emailVal", user.getUsername());
				return "registration";
			}
			if(user.getUsername() == null || user.getUsername().isEmpty())
			{
				model.addAttribute("errorMessage", "error.emptyEmail");
				model.addAttribute("nameVal", user.getName());
				model.addAttribute("passwordVal", user.getPassword());
				return "registration";
			}
			if(user.getName() == null || user.getName().isEmpty())
			{
				model.addAttribute("errorMessage", "error.emptyName");
				model.addAttribute("emailVal", user.getUsername());
				model.addAttribute("passwordVal", user.getPassword());
				return "registration";
			}

			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setActive(true);
			user.setRoles(Collections.singleton(Role.USER));
			user.setRegistrationDate(LocalDateTime.now().toString());
			userDAO.save(user);

			//mailSender.sendGreetingMessage(user.getUsername());
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
	public String forgotPassword(@RequestParam String email)
	{
		String uuid = UUID.randomUUID().toString();
		User user = userDAO.findByUsername(email);
		user.setUpdatePassword(uuid);
		userDAO.save(user);

		mailSender.sendUpdatePasswordMessage(email, uuid);

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
			if(user.getUpdatePassword().equals(updatePassword))
			{
				password = passwordEncoder.encode(password);
				user.setPassword(password);
				userDAO.save(user);
			}

			if(userDAO.findByIdEquals(user.getId()).getPassword().equals(password))
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

		model.addAttribute("thanks", "Thank you for feedback!");

		return "feedback";
	}

}
