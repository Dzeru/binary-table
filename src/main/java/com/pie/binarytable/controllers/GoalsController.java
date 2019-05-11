package com.pie.binarytable.controllers;

import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.GroupGoal;
import com.pie.binarytable.entities.User;
import com.pie.binarytable.repositories.GoalRepository;
import com.pie.binarytable.repositories.GroupGoalRepository;
import com.pie.binarytable.services.AddGoalService;
import com.pie.binarytable.services.GoalService;
import com.pie.binarytable.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
Controller for goals.
REST controller for /goal page is in another class RestGoalsController
*/
@Controller
public class GoalsController
{
	@Autowired
	private GoalRepository goalRepository;

	@Autowired
	private GroupGoalRepository groupGoalRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private AddGoalService addGoalService;

	@Autowired
	private GoalService goalService;

	/*
	Forms list of goals and so on
	*/
	@GetMapping("/goals")
	public String goals(Principal principal,
	                    Model model,
						Device device)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());

		Long userId = user.getId();
		HashSet<Goal> allGoals = new HashSet(goalRepository.findByUserId(userId));
		ArrayList<GroupGoal> groupGoals = new ArrayList(groupGoalRepository.findByUserId(userId));

		if(!groupGoals.isEmpty())
		{
			for(GroupGoal goal : groupGoals)
			{
				allGoals.add(goalRepository.findByIdEquals(goal.getGoalId()));
			}
		}

		goalService.checkCurrentStateOfGoals(allGoals);

		List<Goal> finishedGoals = allGoals.stream().filter((g) -> g.isFinished()).collect(Collectors.toList());
		List<Goal> goals = allGoals.stream().filter((g) -> !g.isFinished()).collect(Collectors.toList());

		model.addAttribute("goals", goals);
		model.addAttribute("finishedGoals", finishedGoals);
		model.addAttribute("user", user);

		if(device.isNormal())
		{
			return "goals";
		}
		else
		{
			return "goalscompact";
		}
	}

	/*
	Gets page /addgoal, where user can add the goal
	*/
	@GetMapping("/addgoal")
	public String newGoal(Principal principal,
	                      Model model,
						  Device device)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());
		model.addAttribute("user", user);

		if(device.isNormal())
		{
			return "addgoal";
		}
		else
		{
			return "addgoalcompact";
		}
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
