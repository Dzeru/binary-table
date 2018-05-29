package com.pie.binarytable.controllers;

import org.springframework.web.bind.annotation.GetMapping;

/*
Controller for simple pages
 */
public class PageController
{
	@GetMapping("/about")
	public String about()
	{
		return "about";
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
}
