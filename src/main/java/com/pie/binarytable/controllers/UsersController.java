package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.entities.Role;
import com.pie.binarytable.entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

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
	public String addUser(User user, Model model)
	{
		User userFromDB = userDAO.findByUsername(user.getUsername());

		if(userFromDB != null)
		{
			model.addAttribute("errorMessage", "error.emailExists");
			return "registration";
		}
		if(user.getPassword().length() < 6)
		{
			model.addAttribute("errorMessage", "error.shortPassword");
			return "registration";
		}
		if(user.getUsername() == null || user.getUsername().isEmpty())
		{
			model.addAttribute("errorMessage", "error.emptyEmail");
			return "registration";
		}
		if(user.getName() == null || user.getName().isEmpty())
		{
			model.addAttribute("errorMessage", "error.emptyName");
			return "registration";
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setActive(true);
		user.setRoles(Collections.singleton(Role.USER));
		userDAO.save(user);

		return "redirect:/login";
	}
}
