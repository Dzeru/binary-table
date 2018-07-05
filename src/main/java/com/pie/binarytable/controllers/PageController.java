package com.pie.binarytable.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
Controller for simple pages
 */
@Controller
public class PageController
{
	@GetMapping("/about")
	public String about()
	{
		return "about";
	}

	@GetMapping("/terms")
	public String terms()
	{
		return "terms";
	}

	@GetMapping("/index")
	public String main()
	{
		return "index";
	}

	@GetMapping("/")
	public String index()
	{
		return "index";
	}

	@GetMapping("/goal")
	public String goal()
	{
		return "goal";
	}

	@GetMapping("/feedback")
	public String feedback()
	{
		return "feedback";
	}

	@GetMapping("/forgotpassword")
	public String forgotpassword()
	{
		return "forgotpassword";
	}
}
