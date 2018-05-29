package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class GoalsController
{
	@Autowired
	private GoalDAO goalDAO;

	@GetMapping("/goals")
	public String goals(@AuthenticationPrincipal User user, Model model)
	{
		//TODO: rewrite repo function for finding by user id
		Iterable<Goal> goals = goalDAO.findAll();

		model.addAttribute("goals", goals);

		return "goals";
	}

	//Add goal
	@PostMapping("/goals")
	public String ad(@AuthenticationPrincipal User user,
	                 @RequestParam String name,
	                 @RequestParam int steps,
	                 @RequestParam String note,
	                 Map<String, Object> model)
	{
		Goal goal = new Goal(user, name, steps, note);

		if(note == null)
			goal.setNote("");

		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < steps; i++)
			sb.append("0");

		goal.setCurrentState(sb.toString());

		goalDAO.save(goal);

		Iterable<Goal> goals = goalDAO.findAll();

		model.put("goals", goals);

		return "goals";
	}
}
