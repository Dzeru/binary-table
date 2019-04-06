package com.pie.binarytable.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.mobile.device.Device;

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
	public String index(Device device)
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
