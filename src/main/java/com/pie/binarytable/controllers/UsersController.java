package com.pie.binarytable.controllers;

import com.pie.binarytable.repositories.UserRepository;
import com.pie.binarytable.entities.User;
import com.pie.binarytable.services.MailSender;
import com.pie.binarytable.services.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

/*
Controller for users
 */
@Controller
public class UsersController
{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailSender mailSender;

	@Autowired
	private SignUpService signUpService;

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
		Map<String, Object> modelSignUp = signUpService.signUp(user, repeatPassword);

		if(modelSignUp.isEmpty())
		{
			return "redirect:/login";
		}
		else
		{
			model.addAllAttributes(modelSignUp);
			return "signup";
		}
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
		User user = userRepository.findByUsername(email);
		if(user != null)
		{
			String uuid = UUID.randomUUID().toString();
			user.setUpdatePassword(uuid);
			userRepository.save(user);

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
			User user = userRepository.findByUsername(email);

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
				userRepository.save(user);
			}

			if(password.equals(userRepository.findByIdEquals(user.getId()).getPassword()))
			{
				status = "status.successUpdatePassword";
				user.setUpdatePassword("");
				userRepository.save(user);
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
	public String feedback(Device device)
	{
		if(device.isNormal())
		{
			return "feedback";
		}
		else
		{
			return "feedbackcompact";
		}
	}

	@PostMapping("/feedback")
	public String sendFeedback(@RequestParam String feedback, Model model, Device device)
	{
		mailSender.sendFeedbackMessage(feedback);

		model.addAttribute("thanks", "feedback.thankYouForFeedback");

		if(device.isNormal())
		{
			return "feedback";
		}
		else
		{
			return "feedbackcompact";
		}
	}

}
