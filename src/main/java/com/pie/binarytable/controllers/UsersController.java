package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.entities.Role;
import com.pie.binarytable.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.Map;

@Controller
public class UsersController
{
	@Autowired
	private UserDAO userDAO;

	@GetMapping("/registration")
	public String registration()
	{
		return "registration";
	}

	@PostMapping("/registration")
	public String addUser(User user, Map<String, Object> model)
	{
		User userFromDB = userDAO.findByUsername(user.getUsername());

		if(userFromDB != null)
		{
			model.put("message", "User already exists!");
			return "registration";
		}

		user.setActive(true);
		user.setRoles(Collections.singleton(Role.USER));
		userDAO.save(user);
System.out.println("RRRRRRRRRRRRRRRRRRRRRRRRRRRR " + user.getName());
		return "redirect:/login";
	}
}
