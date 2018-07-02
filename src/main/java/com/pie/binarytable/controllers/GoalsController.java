package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/*
Controller for goals.
REST controller for /goal page is in another class RestGoalsController
 */
@Controller
public class GoalsController
{
	@Autowired
	private GoalDAO goalDAO;

	/*
	Forms list of goals and so on
	 */
	@GetMapping("/goals")
	public String goals(@AuthenticationPrincipal User user, Model model)
	{
		ArrayList<Goal> goals = new ArrayList(goalDAO.findByUserId(user.getId()));

		int count = goals.size();
		String countOfGoals = count == 1 ? count + " goal" : count + " goals";

		model.addAttribute("goals", goals);
		model.addAttribute("user", user);
		model.addAttribute("countOfGoals", countOfGoals);

		return "goals";
	}

	/*
	Gets page /new goal
	 */
	@GetMapping("/newgoal")
	public String newGoal(@AuthenticationPrincipal User user, Model model)
	{
		model.addAttribute("user", user);
		return "newgoal";
	}

	/*
	Adds new goal and redirect to the list of user's goals
	 */
	@PostMapping("/newgoal")
	public String addGoal(@AuthenticationPrincipal User user,
	                 @RequestParam String name,
	                 @RequestParam Integer steps,
	                 @RequestParam String note,
	                 Model model)
	{
		if(name == null || name.isEmpty())
		{
			model.addAttribute("errorMessage", "Goal name is empty!");
			return "newgoal";
		}
		if(steps == null || steps <= 0)
		{
			model.addAttribute("errorMessage", "Amount of steps must be greater than 0!");
			return "newgoal";
		}

		Goal goal = new Goal(user.getId(), name, steps, note);

		if(note == null)
			goal.setNote("");

		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < steps; i++)
			sb.append("0");

		goal.setCurrentState(sb.toString());

		goalDAO.save(goal);

		return "redirect:/goals";
	}
}
