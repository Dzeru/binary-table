package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.dao.GroupGoalDAO;
import com.pie.binarytable.dao.UserAccountsDAO;
import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.dto.AddGoalReturnParams;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.GroupGoal;
import com.pie.binarytable.entities.User;

import com.pie.binarytable.entities.UserAccounts;
import com.pie.binarytable.services.AddGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.sql.Timestamp;
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
	private UserDAO userDAO;

	@Autowired
	private UserAccountsDAO userAccountsDAO;

	@Autowired
	private AddGoalService addGoalService;

	/*
	Forms list of goals and so on
	 */
	@GetMapping("/goals")
	public String goals(@AuthenticationPrincipal User user, Model model)
	{
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
	Forms list of goals and so on
	 */
	@GetMapping("/goalssso")
	public String goalssso(@AuthenticationPrincipal Principal principal, Model model)
	{
		String principalName = principal.getName();

		UserAccounts userAccounts = userAccountsDAO.findByGoogleUsername(principalName);

		if(userAccounts != null)
		{
			User user = userDAO.findByUserAccountsId(userAccounts.getId());

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
		}
		return "goals";
	}

	/*
	Gets page /addgoal, where user can add the goal
	 */
	@GetMapping("/addgoal")
	public String newGoal(@AuthenticationPrincipal User user, Model model)
	{
		model.addAttribute("user", user);
		return "addgoal";
	}

	/*
	Adds new goal and redirect back to the list of user's goals
	 */
	@PostMapping("/addgoal")
	public String addGoal(@AuthenticationPrincipal User user,
						  @RequestParam String goalName,
						  @RequestParam String steps,
						  @RequestParam(required = false) String note,
						  @RequestParam(required = false) String emails,
						  Model model)
	{
		AddGoalReturnParams addGoalReturnParams = addGoalService.addGoal(user, goalName, steps, note, emails);

		HashMap<String, Object> params = addGoalReturnParams.getParams();

		if(!params.isEmpty())
		{
			for(Map.Entry<String, Object> par : params.entrySet())
			{
				model.addAttribute(par.getKey(), par.getValue());
			}
		}

		return addGoalReturnParams.getUrl();
	}
}
