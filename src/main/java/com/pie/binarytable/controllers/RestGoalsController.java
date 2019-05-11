package com.pie.binarytable.controllers;

import com.pie.binarytable.services.UserService;
import com.pie.binarytable.repositories.GoalRepository;
import com.pie.binarytable.repositories.GroupGoalRepository;
import com.pie.binarytable.repositories.UserRepository;
import com.pie.binarytable.dto.CollaboratorsList;
import com.pie.binarytable.dto.GoalId;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.GroupGoal;
import com.pie.binarytable.entities.User;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;

@RestController
public class RestGoalsController
{
	private final static Logger logger = Logger.getLogger(RestGoalsController.class);

	@Autowired
	GoalRepository goalRepository;

	@Autowired
	GroupGoalRepository groupGoalRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	/*
	Sends JSON with Goal object to JavaScript functions in /goal
    */
	@RequestMapping(value = "/getgoal", method = RequestMethod.GET)
	public Goal goal(Principal principal,
	                 @RequestParam(value="id") Long goalId)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());
		Goal goal = goalRepository.findByIdEquals(goalId);

		if(goal.getUserId().equals(user.getId()))
		{
			logger.info("Get goal: user id = " + user.getId() + ", goal's userId = " + goal.getUserId());
			return goal;
		}
		else
		{
			logger.warn("Get goal: user with id = " + user.getId() + " try to access goal with userId = " + goal.getUserId());
			return null;
		}
	}

	@RequestMapping(value = "/updategoal", method = RequestMethod.POST)
	public ResponseEntity updateGoal(Principal principal,
	                                 @RequestBody Goal goal)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());
	
		if(!goal.getUserId().equals(user.getId()))
		{
			logger.warn("Update goal: user with id = " + user.getId() + " try to access goal with userId = " + goal.getUserId());
		}
		else
		{
			if(goal.isFinished() && goal.getDoneSteps() != goal.getAllSteps())
			{
				goal.setFinished(false);
			}

			goalRepository.save(goal);

			if(goalRepository.findByIdEquals(goal.getId()).getCurrentState().equals(goal.getCurrentState()))
			{
				logger.info("Update goal: goal with id = " + goal.getId() + " is successfully updated");
				return ResponseEntity.ok().body("Goal is successfully updated");
			}
			else return ResponseEntity.badRequest().body("Fail to update goal");
		}
	}

	/*
	Deletes goal by goal id
	 */
	@RequestMapping(value = "/deletegoal", method = RequestMethod.POST)
	public ResponseEntity deleteGoal(Principal principal,
	                                 @RequestBody GoalId goalId)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());

		Long id = goalId.getId();
		Goal goal = goalRepository.findByIdEquals(id);

		if(!goal.getUserId().equals(user.getId()))
		{
			logger.warn("Delete goal: user with id = " + user.getId() + " try to access goal with userId = " + goal.getUserId());
			return ResponseEntity.badRequest().body("No access to this method");
		}
		else
		{
			if(goal.isGroupGoal())
			{
				groupGoalRepository.deleteByGoalId(id);

				if(!groupGoalRepository.findByGoalId(id).isEmpty()) //list!
				{
					logger.warn("Delete goal: fail to delete group goal with id = " + id);
					return ResponseEntity.badRequest().body("Fail to delete group goal with id = " + id);
				}
			}

			goalRepository.deleteById(id);

			if(goalRepository.findByIdEquals(id) == null) //one object!
			{
				logger.info("Delete goal: goal is successfully deleted, goals' id = " + goal.getId());
				return ResponseEntity.ok().body("Goal is successfully deleted");
			}
			else
			{
				logger.warn("Delete goal: fail to delete goal with id = " + id);
				return ResponseEntity.badRequest().body("Fail to delete goal with id = " + id);
			}
		}
	}

	@RequestMapping(value = "/getcollaborators", method = RequestMethod.GET)
	public CollaboratorsList getCollaborators(Principal principal,
	                                          @RequestParam(value = "id") Long id)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());

		ArrayList<GroupGoal> groupGoals = groupGoalRepository.findByGoalId(id);
		CollaboratorsList collaboratorsList = new CollaboratorsList();

		boolean isCollaborator = false;

		for(GroupGoal g : groupGoals)
		{
			if(g.getUserId().equals(user.getId()))
				isCollaborator = true;
			collaboratorsList.addCollaborator(userRepository.findByIdEquals(g.getUserId()).getUsername());
		}

		if(isCollaborator)
		{
			logger.info("Get collaborators: goal's id = " + groupGoals.get(0).getGoalId());
			return collaboratorsList;
		}
		else
		{
			logger.info("Get collaborators: fail to get collaborators, goal's id = " + groupGoals.get(0).getGoalId());
			return null;
		}
	}
}
