package com.pie.binarytable.services;

import com.pie.binarytable.repositories.UserRepository;
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
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		User userFindByUsername = userRepository.findByUsername(username);
		User userFindByName = userRepository.findByName(username);
		User userFindByGoogleUsername = userRepository.findByGoogleUsername(username);
		User userFindByGoogleName = userRepository.findByGoogleName(username);

		if(userFindByUsername != null)
		{
			return userFindByUsername;
		}

		if(userFindByName != null)
		{
			return userFindByName;
		}

		if(userFindByGoogleUsername != null)
		{
			return userFindByGoogleUsername;
		}

		if(userFindByGoogleName != null)
		{
			return userFindByGoogleName;
		}

		return null;
	}
}
