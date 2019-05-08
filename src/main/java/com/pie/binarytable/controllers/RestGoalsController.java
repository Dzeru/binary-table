package com.pie.binarytable.controllers;

import com.pie.binarytable.repositories.GoalRepository;
import com.pie.binarytable.repositories.GroupGoalRepository;
import com.pie.binarytable.repositories.UserRepository;
import com.pie.binarytable.dto.CollaboratorsList;
import com.pie.binarytable.dto.GoalId;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.GroupGoal;
import com.pie.binarytable.entities.User;

import com.pie.binarytable.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;

@RestController
public class RestGoalsController
{
	@Autowired
	GoalRepository goalRepository;

	@Autowired
	GroupGoalRepository groupGoalRepository;

	@Autowired
	UserRepository userDAO;

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

		if(goal.getUserId() == user.getId())
			return goalRepository.findByIdEquals(goalId);
		else return null;
	}

	@RequestMapping(value = "/updategoal", method = RequestMethod.POST)
	public ResponseEntity updateGoal(Principal principal,
	                                 @RequestBody Goal goal)
	{
		User user = (User) userService.loadUserByUsername(principal.getName());

		if(goal.getUserId() != user.getId())
			return ResponseEntity.badRequest().body("No access to this method");
		else
		{
			goalRepository.save(goal);

			if(goalRepository.findByIdEquals(goal.getId()).getCurrentState().equals(goal.getCurrentState()))
			{
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

		if(goalRepository.findByIdEquals(id).getUserId() != user.getId())
		{
			return ResponseEntity.badRequest().body("No access to this method");
		}
		else
		{
			if(goalRepository.findByIdEquals(id).isGroupGoal())
			{
				groupGoalRepository.deleteByGoalId(id);

				if(!groupGoalRepository.findByGoalId(id).isEmpty()) //list!
				{
					return ResponseEntity.badRequest().body("Fail to delete group goal");
				}
			}

			goalRepository.deleteById(id);

			if(goalRepository.findByIdEquals(id) == null) //one object!
			{
				return ResponseEntity.ok().body("Goal is successfully deleted");
			} else return ResponseEntity.badRequest().body("Fail to delete goal");
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
			if(g.getUserId() == user.getId())
				isCollaborator = true;
			collaboratorsList.addCollaborator(userDAO.findByIdEquals(g.getUserId()).getUsername());
		}

		if(isCollaborator)
			return collaboratorsList;
		else
			return null;
	}
}
