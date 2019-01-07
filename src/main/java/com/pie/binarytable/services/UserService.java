package com.pie.binarytable.services;

import com.pie.binarytable.dao.UserDAO;

import com.pie.binarytable.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService
{
	@Autowired
	private UserDAO userDAO;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		User userFindByUsername = userDAO.findByUsername(username);
		User userFindByName = userDAO.findByName(username);
		if(userFindByUsername == null)
			return userFindByName;
		else return userFindByUsername;
	}
}
