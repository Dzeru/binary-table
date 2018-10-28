package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.dao.GroupGoalDAO;
import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.dto.CollaboratorsList;
import com.pie.binarytable.dto.GoalId;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.GroupGoal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	public Goal goal(@RequestParam(value="id") Long goalId)
	{
		return goalDAO.findByIdEquals(goalId);
	}

	/*
	Updates @Param currentState of goal from /goal page
	 */
	@RequestMapping(value = "/updategoal", method = RequestMethod.POST)
	public ResponseEntity updateGoal(@RequestBody Goal goal)
	{
		boolean result = false; //error by default

		goalDAO.save(goal);

		if(goalDAO.findByIdEquals(goal.getId()).getCurrentState().equals(goal.getCurrentState()))
		{
			result = true; //success
			return ResponseEntity.ok().body(result);
		}
		else return ResponseEntity.badRequest().body(result);
	}

	/*
	Deletes goal by goal id
	 */
	@RequestMapping(value = "/deletegoal", method = RequestMethod.POST)
	public ResponseEntity deleteGoal(@RequestBody GoalId goalId)
	{
		boolean result = false; //error by default
		Long id = goalId.getId();

		if(goalDAO.findByIdEquals(id).isGroupGoal())
		{
			groupGoalDAO.deleteByGoalId(id);

			if(!groupGoalDAO.findByGoalId(id).isEmpty()) //list!
			{
				return ResponseEntity.badRequest().body(result);
			}
		}

		goalDAO.deleteById(id);

		if(goalDAO.findByIdEquals(id) == null) //one object!
		{
			result = true; //success
			return ResponseEntity.ok().body(result);
		}
		else return ResponseEntity.badRequest().body(result);
	}

	@RequestMapping(value = "/getcollaborators", method = RequestMethod.GET)
	public CollaboratorsList getCollaborators(@RequestParam(value = "id") Long id)
	{
		ArrayList<GroupGoal> groupGoals = groupGoalDAO.findByGoalId(id);
		CollaboratorsList collaboratorsList = new CollaboratorsList();

		for(GroupGoal g : groupGoals)
		{
			collaboratorsList.addCollaborator(userDAO.findByIdEquals(g.getUserId()).getUsername());
		}
		return collaboratorsList;
	}
}
