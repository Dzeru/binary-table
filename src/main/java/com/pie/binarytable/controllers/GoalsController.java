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
TODO: Rewrite error messages from /newgoal in i18n style
*/

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

		int countOfGoals = goals.size();

		model.addAttribute("goals", goals);
		model.addAttribute("user", user);
		model.addAttribute("countOfGoals", countOfGoals);

		return "goals";
	}

	/*
	Gets page /addgoal, where user can add the goal
	 */
	@GetMapping("/addgoal")
	public String newGoal(Model model)
	{
		return "addgoal";
	}

	/*
	Adds new goal and redirect back to the list of user's goals
	 */
	@PostMapping("/addgoal")
	public String addGoal(@AuthenticationPrincipal User user,
	                 @RequestParam String name,
	                 @RequestParam Integer steps,
	                 @RequestParam String note,
	                 Model model)
	{
		if(name == null || name.isEmpty())
		{
			model.addAttribute("errorMessage", "Goal name is empty!");
			return "addgoal";
		}
		if(steps == null || steps <= 0)
		{
			model.addAttribute("errorMessage", "Amount of steps must be greater than 0!");
			return "addgoal";
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
