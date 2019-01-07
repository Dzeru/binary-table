package com.pie.binarytable.controllers;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RestController
public class OAuth2Controller
{
	@RequestMapping(value = "/isloginwithsso", method= RequestMethod.GET)
	public boolean user(Principal principal)
	{
		return principal != null;
	}
}
