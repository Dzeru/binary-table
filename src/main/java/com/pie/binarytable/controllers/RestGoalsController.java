package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.entities.Goal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestGoalsController
{
	@Autowired
	GoalDAO goalDAO;

	/*
	Sends JSON with Goal object to JavaScript functions in /goal
    */
	@RequestMapping("/getgoal")
	public Goal goal(@RequestParam(value="id") Long goalId)
	{
		return goalDAO.findByIdEquals(goalId);
	}
}
