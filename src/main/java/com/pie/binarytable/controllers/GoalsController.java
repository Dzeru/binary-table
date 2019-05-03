package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.dao.GroupGoalDAO;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.GroupGoal;
import com.pie.binarytable.entities.User;
import com.pie.binarytable.services.AddGoalService;
import com.pie.binarytable.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;
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
	private UserService userService;

	@Autowired
	private AddGoalService addGoalService;

	/*
	Forms list of goals and so on
	 */
	@GetMapping("/goals")
	public String goals(Principal principal,
	                    Model model)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());

		Long userId = user.getId();
		HashSet<Goal> allGoals = new HashSet(goalDAO.findByUserId(userId));
		ArrayList<GroupGoal> groupGoals = new ArrayList(groupGoalDAO.findByUserId(userId));

		if(!groupGoals.isEmpty())
		{
			for(GroupGoal goal : groupGoals)
			{
				allGoals.add(goalDAO.findByIdEquals(goal.getGoalId()));
			}
		}

		List<Goal> finishedGoals = allGoals.stream().filter((g) -> g.isFinished()).collect(Collectors.toList());
		List<Goal> goals = allGoals.stream().filter((g) -> !g.isFinished()).collect(Collectors.toList());

		model.addAttribute("goals", goals);
		model.addAttribute("finishedGoals", finishedGoals);
		model.addAttribute("user", user);

		return "goals";
	}

	/*
	Gets page /addgoal, where user can add the goal
	 */
	@GetMapping("/addgoal")
	public String newGoal(Principal principal,
	                      Model model)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());
		model.addAttribute("user", user);
		return "addgoal";
	}

	/*
	Adds new goal and redirect back to the list of user's goals
	 */
	@PostMapping("/addgoal")
	public String addGoal(Principal principal,
						  @RequestParam String goalName,
						  @RequestParam String steps,
						  @RequestParam(required = false) String note,
						  @RequestParam(required = false) String emails,
						  Model model)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());
		Map<String, Object> modelAddGoal = addGoalService.addGoal(user, goalName, steps, note, emails);

		if(modelAddGoal.isEmpty())
		{
			return "redirect:/goals";
		}
		else
		{
			model.addAllAttributes(modelAddGoal);
			return "addgoal";
		}
	}
}
