package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.entities.Goal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestGoalsController
{
	@Autowired
	GoalDAO goalDAO;

	@RequestMapping("/getgoal")
	public Goal goal(Long goalId)
	{
		return goalDAO.findByIdEquals(goalId);
	}
}
