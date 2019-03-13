package com.pie.binarytable.controllers;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.dao.GroupGoalDAO;
import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.dto.CollaboratorsList;
import com.pie.binarytable.dto.GoalId;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.GroupGoal;
import com.pie.binarytable.entities.User;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class RestGoalsController
{
	private final static Logger logger = Logger.getLogger(RestGoalsController.class);

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
	public ResponseEntity updateGoal(@RequestBody Goal goal, @AuthenticationPrincipal User user)
	{
		if(!goal.getUserId().equals(user.getId()))
		{
			logger.warn("Update goal: user with id = " + user.getId() + " try to access goal with userId = " + goal.getUserId());
			return ResponseEntity.badRequest().body("No access to this method");
		}
		else
		{
			if(goal.isFinished() && goal.getDoneSteps() != goal.getAllSteps())
			{
				goal.setFinished(false);
			}

			goalDAO.save(goal);

			if(goalDAO.findByIdEquals(goal.getId()).getCurrentState().equals(goal.getCurrentState()))
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
	public ResponseEntity deleteGoal(@RequestBody GoalId goalId, @AuthenticationPrincipal User user)
	{
		Long id = goalId.getId();
		Goal goal = goalDAO.findByIdEquals(id);

		if(!goal.getUserId().equals(user.getId()))
		{
			logger.warn("Delete goal: user with id = " + user.getId() + " try to access goal with userId = " + goal.getUserId());
			return ResponseEntity.badRequest().body("No access to this method");
		}
		else
		{
			if(goal.isGroupGoal())
			{
				groupGoalDAO.deleteByGoalId(id);

				if(!groupGoalDAO.findByGoalId(id).isEmpty()) //list!
				{
					logger.warn("Delete goal: fail to delete group goal with id = " + id);
					return ResponseEntity.badRequest().body("Fail to delete group goal with id = " + id);
				}
			}

			goalDAO.deleteById(id);

			if(goalDAO.findByIdEquals(id) == null) //one object!
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
	public CollaboratorsList getCollaborators(@RequestParam(value = "id") Long id, @AuthenticationPrincipal User user)
	{
		ArrayList<GroupGoal> groupGoals = groupGoalDAO.findByGoalId(id);
		CollaboratorsList collaboratorsList = new CollaboratorsList();

		boolean isCollaborator = false;

		for(GroupGoal g : groupGoals)
		{
			if(g.getUserId().equals(user.getId()))
				isCollaborator = true;
			collaboratorsList.addCollaborator(userDAO.findByIdEquals(g.getUserId()).getUsername());
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
