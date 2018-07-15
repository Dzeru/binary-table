package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.entities.Goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RestGoalsController
{
	@Autowired
	GoalDAO goalDAO;

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
		return ResponseEntity.badRequest().body(result);
	}

	/*
	Deletes goal by goal id
	 */
	@RequestMapping(value = "/deletegoal", method = RequestMethod.POST)
	public ResponseEntity deleteGoal(@RequestParam(value = "id") Long goalId)
	{
		boolean result = false; //error by default
		goalDAO.deleteById(goalId);

		if(goalDAO.findByIdEquals(goalId) == null)
		{
			result = true; //success
			return ResponseEntity.ok().body(result);
		}
		return ResponseEntity.badRequest().body(result);
	}
}
