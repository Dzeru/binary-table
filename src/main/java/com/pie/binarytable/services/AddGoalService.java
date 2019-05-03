package com.pie.binarytable.services;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.dao.GroupGoalDAO;
import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.GroupGoal;
import com.pie.binarytable.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AddGoalService
{
    @Autowired
    GoalDAO goalDAO;
    
    @Autowired
    GroupGoalDAO groupGoalDAO;

    @Autowired
    UserDAO userDAO;

    public Map<String, Object> addGoal(User user,
                                       String goalName,
                                       String steps,
                                       String note,
                                       String emails)
    {
        Map<String, Object> model = new HashMap<>();
        
        int st = 0;

        try
        {
            st = Integer.parseInt(steps);

            if(st <= 0 || st > 625)
                throw new Exception();
        }
        catch(Exception e)
        {
            model.put("error", "error.wrongSteps");
            model.put("goalNameVal", goalName);
            model.put("noteVal", note);
            model.put("user", user);
        }

        if(goalName == null || goalName.trim().isEmpty())
        {
            model.put("error", "error.emptyGoal");
            model.put("stepsVal", steps);
            model.put("noteVal", note);
            model.put("user", user);
        }

        if(goalName.length() > 65535)
        {
            model.put("error", "error.tooLongGoalName");
            model.put("goalNameVal", goalName);
            model.put("stepsVal", steps);
            model.put("noteVal", note);
            model.put("user", user);
        }

        if(note.length() > 65535)
        {
            model.put("error", "error.tooLongNote");
            model.put("goalNameVal", goalName);
            model.put("stepsVal", steps);
            model.put("noteVal", note);
            model.put("user", user);
        }

        String goalTimestamp = new Timestamp(System.currentTimeMillis()).toString();

        Goal newGoal = new Goal(user.getId(), goalName, st, note, goalTimestamp);

        if(note == null)
            newGoal.setNote("");

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < st; i++)
            sb.append("0");

        newGoal.setCurrentState(sb.toString());
        newGoal.setFinished(false);

        UUID hash = UUID.randomUUID();
        newGoal.setHash(hash.toString());

        goalDAO.save(newGoal);

		/*
		Only for group goals
		 */
        if(!emails.isEmpty() && emails != null)
        {
            newGoal.setGroupGoal(true);
            goalDAO.save(newGoal);
            String[] emailList = emails.split(", ");
            newGoal = goalDAO.findByGoalNameEqualsAndUserIdEqualsAndGoalTimestampEquals(goalName, user.getId(), goalTimestamp);
            Long goalId = newGoal.getId();

            for(String email : emailList)
            {
                User username = userDAO.findByUsername(email);
                if(username != null)
                {
                    GroupGoal groupGoal = new GroupGoal(goalId, username.getId());
                    groupGoalDAO.save(groupGoal);
                }
                else
                {
                    model.put("error", "error.emailDoesNotExist");
                    model.put("user", user);
                }
            }
        }
        goalDAO.save(newGoal);

        return model;
    }
}
