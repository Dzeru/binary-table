package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.UserAccountsDAO;
import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.entities.Role;
import com.pie.binarytable.entities.User;
import com.pie.binarytable.entities.UserAccounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@RestController
public class OAuth2Controller
{
	@Autowired
	UserAccountsDAO userAccountsDAO;

	@Autowired
	UserDAO userDAO;

	@Autowired
	PasswordEncoder encoder;

	@RequestMapping(value = "/isloginwithsso", method = RequestMethod.GET)
	public boolean user(@AuthenticationPrincipal Principal principal)
	{
		UserAccounts userAccounts = userAccountsDAO.findByGoogleUsername(principal.getName());

		if(userAccounts == null)
		{
			User user = new User();

			userAccounts = new UserAccounts();
			userAccounts.setBinaryTableUsername("");
			userAccounts.setBinaryTableName(principal.getName());
			userAccounts.setGoogleUsername(principal.getName());
			userAccountsDAO.save(userAccounts);

			userAccounts = userAccountsDAO.findByGoogleUsername(principal.getName());

			user.setUserAccountsId(userAccounts.getId());
			user.setName("");
			user.setUsername("");
			user.setPassword(encoder.encode(UUID.randomUUID().toString()));
			user.setActive(true);
			user.setRoles(Collections.singleton(Role.USER));
			user.setRegistrationDate(LocalDateTime.now().toString());

			userDAO.save(user);
		}

		return userAccounts != null;
	}
}
