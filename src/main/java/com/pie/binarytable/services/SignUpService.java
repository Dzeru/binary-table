package com.pie.binarytable.services;

import com.pie.binarytable.repositories.UserRepository;
import com.pie.binarytable.entities.Role;
import com.pie.binarytable.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class SignUpService
{
	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	MailSender mailSender;

	public Map<String, Object> signUp(User user, String repeatPassword)
	{
		Map<String, Object> model = new HashMap<>();

		if(!user.getPassword().equals(repeatPassword))
		{
			model.put("error", "error.equalPasswords");
			model.put("nameVal", user.getName());
			model.put("emailVal", user.getUsername());
		}
		else
		{
			User userFromDB = userRepository.findByUsername(user.getUsername());

			if(userFromDB != null)
			{
				model.put("error", "error.emailExists");
				model.put("nameVal", user.getName());
			}
			if(user.getPassword().length() < 6)
			{
				model.put("error", "error.shortPassword");
				model.put("nameVal", user.getName());
				model.put("emailVal", user.getUsername());
			}
			if(user.getUsername() == null || user.getUsername().isEmpty())
			{
				model.put("error", "error.emptyEmail");
				model.put("nameVal", user.getName());
			}
			if(user.getName() == null || user.getName().isEmpty())
			{
				model.put("error", "error.emptyName");
				model.put("emailVal", user.getUsername());
			}

			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setActive(true);
			user.setRoles(Collections.singleton(Role.USER));

			user.setRegistrationDate(LocalDateTime.now().toString());

			userRepository.save(user);

			mailSender.sendGreetingMessage(user.getUsername(), user.getName());
		}

		return model;
	}
}
