package com.pie.binarytable.controllers;

import com.pie.binarytable.entities.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.mobile.device.Device;

import java.security.Principal;

/*
Controller for simple pages
 */
@Controller
public class PageController
{
	@GetMapping("/terms")
	public String terms()
	{
		return "terms";
	}

	@GetMapping("/#")
	public String mainOAuth2(Principal principal)
	{
		if(principal != null)
		{
			return "redirect:/goals";
		}
		return "index";
	}

	@GetMapping("/index")
	public String main(Device device)
	{
		if(device.isNormal())
		{
			return "index";
		}
		else
		{
			return "indexcompact";
		}
	}

	@GetMapping("/")
	public String index(Principal principal, Device device)
	{
		if(principal != null)
		{
			return "redirect:/goals";
		}
		
		if(device.isNormal())
		{
			return "index";
		}
		else
		{
			return "indexcompact";
		}
	}

	@GetMapping("/goal")
	public String goal()
	{
		return "goal";
	}

	@GetMapping("/contacts")
	public String contact()
	{
		return "contacts";
	}

	@GetMapping("/usecases")
	public String useCases()
	{
		return "usecases";
	}
}
