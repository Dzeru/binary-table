package com.pie.binarytable.services;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.entities.Goal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class GoalService
{
	@Autowired
	private GoalDAO goalDAO;

	public void checkCurrentStateOfGoals(HashSet<Goal> goals)
	{
		/*
		Checks if current state of goal is hacked and contains something not equal 0 or 1
		*/
		String checkNotNumbers = "";
		for(Goal g : goals)
		{
			checkNotNumbers = g.getCurrentState().replace("0", "").replace("1", "");

			if(!checkNotNumbers.isEmpty())
			{
				StringBuilder sb = new StringBuilder();

				for(int i = 0; i < g.getDoneSteps(); i++)
				{
					sb.append("1");
				}

				for(int i = g.getDoneSteps(); i < g.getAllSteps(); i++)
				{
					sb.append("0");
				}

				g.setCurrentState(sb.toString());
				goalDAO.save(g);
			}
		}
	}
}
