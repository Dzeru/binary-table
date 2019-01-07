package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.dao.GroupGoalDAO;
import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.dto.CollaboratorsList;
import com.pie.binarytable.dto.GoalId;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.GroupGoal;
import com.pie.binarytable.entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class RestGoalsController
{
	@Autowired
	GoalDAO goalDAO;

	@Autowired
	GroupGoalDAO groupGoalDAO;

	@Autowired
	UserDAO userDAO;

	/*
	Sends JSON with Goal object to JavaScript functions in /goal
    */
	@RequestMapping(value = "/getgoal", method = RequestMethod.GET)
	public Goal goal(@RequestParam(value="id") Long goalId, @AuthenticationPrincipal User user)
	{
		Goal goal = goalDAO.findByIdEquals(goalId);

		if(goal.getUserId() == user.getId())
			return goalDAO.findByIdEquals(goalId);
		else return null;
	}

	@RequestMapping(value = "/updategoal", method = RequestMethod.POST)
	public ResponseEntity updateGoal(@RequestBody Goal goal, @AuthenticationPrincipal User user)
	{
		if(goal.getUserId() != user.getId())
			return ResponseEntity.badRequest().body("No access to this method");
		else
		{
			goalDAO.save(goal);

			if(goalDAO.findByIdEquals(goal.getId()).getCurrentState().equals(goal.getCurrentState()))
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
	public ResponseEntity deleteGoal(@RequestBody GoalId goalId, @AuthenticationPrincipal User user)
	{
		Long id = goalId.getId();

		if(goalDAO.findByIdEquals(id).getUserId() != user.getId())
		{
			return ResponseEntity.badRequest().body("No access to this method");
		}
		else
		{
			if(goalDAO.findByIdEquals(id).isGroupGoal())
			{
				groupGoalDAO.deleteByGoalId(id);

				if(!groupGoalDAO.findByGoalId(id).isEmpty()) //list!
				{
					return ResponseEntity.badRequest().body("Fail to delete group goal");
				}
			}

			goalDAO.deleteById(id);

			if(goalDAO.findByIdEquals(id) == null) //one object!
			{
				return ResponseEntity.ok().body("Goal is successfully deleted");
			} else return ResponseEntity.badRequest().body("Fail to delete goal");
		}
	}

	@RequestMapping(value = "/getcollaborators", method = RequestMethod.GET)
	public CollaboratorsList getCollaborators(@RequestParam(value = "id") Long id, @AuthenticationPrincipal User user)
	{
		ArrayList<GroupGoal> groupGoals = groupGoalDAO.findByGoalId(id);
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
