package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.dao.GroupGoalDAO;
import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.User;
import com.pie.binarytable.entities.GroupGoal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
Controller for goals.
REST controller for /goal page is in another class RestGoalsController
 */
@Controller
public class GoalsController
{
	@Autowired
	private GoalDAO goalDAO;

	@Autowired
	private GroupGoalDAO groupGoalDAO;

	@Autowired
	private UserDAO userDAO;

	/*
	Forms list of goals and so on
	 */
	@GetMapping("/goals")
	public String goals(@AuthenticationPrincipal User user, Model model)
	{
		Long userId = user.getId();
		ArrayList<Goal> allGoals = new ArrayList(goalDAO.findByUserId(userId));

		/*ArrayList<GroupGoal> groupGoals = new ArrayList(groupGoalDAO.findByUserId(userId));

		if(!groupGoals.isEmpty())
		{
			for(GroupGoal goal : groupGoals)
			{
				goals.add(goalDAO.findByIdEquals(goal.getGoalId()));
			}
		}*/

		List<Goal> finishedGoals = allGoals.stream().filter((g) -> g.isFinished()).collect(Collectors.toList());
		List<Goal> goals = allGoals.stream().filter((g) -> !g.isFinished()).collect(Collectors.toList());

		int countOfGoals = goals.size();

		model.addAttribute("goals", goals);
		model.addAttribute("finishedGoals", finishedGoals);
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
	                 @RequestParam(required = false) String note,
	                 @RequestParam(required = false) String emails,
	                 Model model)
	{
		if(name == null || name.isEmpty())
		{
			model.addAttribute("error", "error.emptyGoal");
			model.addAttribute("stepsVal", steps);
			model.addAttribute("noteVal", note);
			return "addgoal";
		}
		if(steps == null || steps <= 0)
		{
			model.addAttribute("error", "error.wrongSteps");
			model.addAttribute("goalNameVal", name);
			model.addAttribute("noteVal", note);
			return "addgoal";
		}

		Goal goal = new Goal(user.getId(), name, steps, note);

		if(note == null)
			goal.setNote("");

		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < steps; i++)
			sb.append("0");

		goal.setCurrentState(sb.toString());
		goal.setFinished(false);

		goalDAO.save(goal);

		/*
		Only for group goals
		 */
		/*if(!emails.isEmpty() && emails != null)
		{
			String[] emailList = emails.split(", ");
			Goal newGoal = goalDAO.findByGoalNameEqualsAndUserIdEquals(name, user.getId());

			for(String email : emailList)
			{
				User username = userDAO.findByUsername(email);
				if(username != null)
				{
					GroupGoal groupGoal = new GroupGoal(newGoal.getId(), username.getId());
					groupGoalDAO.save(groupGoal);
				}
				else
				{
					groupGoalDAO.deleteByGoalId(newGoal.getId());
					goalDAO.deleteById(newGoal.getId());
					model.addAttribute("error", "Email does not exist");
				}
			}
		}*/

		return "redirect:/goals";
	}
}
