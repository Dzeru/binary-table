package com.pie.binarytable.controllers;

import com.pie.binarytable.entities.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
		System.out.println("#");
		if(principal != null)
		{
			System.out.println("##");
			return "redirect:/goals";
		}
		System.out.println("###");
		return "index";
	}

	@GetMapping("/index")
	public String main()
	{
		return "index";
	}

	@GetMapping("/")
	public String index(Principal principal)
	{

		System.out.println("#");
		if(principal != null)
		{
			System.out.println("##");
			return "redirect:/goals";
		}
		System.out.println("###");
		return "index";
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
