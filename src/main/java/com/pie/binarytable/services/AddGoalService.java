package com.pie.binarytable.services;

import com.pie.binarytable.dao.GoalDAO;
import com.pie.binarytable.dao.GroupGoalDAO;
import com.pie.binarytable.dao.UserDAO;
import com.pie.binarytable.dto.AddGoalReturnParams;
import com.pie.binarytable.entities.Goal;
import com.pie.binarytable.entities.GroupGoal;
import com.pie.binarytable.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
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

    /*
    First Entry is URL, others are params for page
     */
    public AddGoalReturnParams addGoal(@AuthenticationPrincipal User user,
                                       String goalName,
                                       String steps,
                                       String note,
                                       String emails)
    {
        AddGoalReturnParams addGoalReturnParams = new AddGoalReturnParams();

        HashMap<String, Object> params = new HashMap<>();
        
        int st = 0;

        try
        {
            st = Integer.parseInt(steps);

            if(st <= 0 || st > 625)
                throw new Exception();
        }
        catch(Exception e)
        {
            params.put("error", "error.wrongSteps");
            params.put("goalNameVal", goalName);
            params.put("noteVal", note);
            params.put("user", user);

            addGoalReturnParams.setParams(params);
            addGoalReturnParams.setUrl("addgoal");
            return addGoalReturnParams;
        }

        if(goalName == null || goalName.trim().isEmpty())
        {
            params.put("error", "error.emptyGoal");
            params.put("stepsVal", steps);
            params.put("noteVal", note);
            params.put("user", user);

            addGoalReturnParams.setParams(params);
            addGoalReturnParams.setUrl("addgoal");
            return addGoalReturnParams;
        }

        if(goalName.length() > 65535)
        {
            params.put("error", "error.tooLongGoalName");
            params.put("goalNameVal", goalName);
            params.put("stepsVal", steps);
            params.put("noteVal", note);
            params.put("user", user);

            addGoalReturnParams.setParams(params);
            addGoalReturnParams.setUrl("addgoal");
            return addGoalReturnParams;
        }

        if(note.length() > 65535)
        {
            params.put("error", "error.tooLongNote");
            params.put("goalNameVal", goalName);
            params.put("stepsVal", steps);
            params.put("noteVal", note);
            params.put("user", user);

            addGoalReturnParams.setParams(params);
            addGoalReturnParams.setUrl("addgoal");
            return addGoalReturnParams;
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
                    params.put("error", "error.emailDoesNotExist");
                    params.put("user", user);

                    addGoalReturnParams.setParams(params);
                    addGoalReturnParams.setUrl("addgoal");
                    return addGoalReturnParams;
                }
            }
        }
        goalDAO.save(newGoal);

        addGoalReturnParams.setParams(params);
        addGoalReturnParams.setUrl("redirect:/goals");

        return addGoalReturnParams;
    }

    /*
    First Entry is URL, others are params for page
     */
   /* public AddGoalReturnParams addGoalSso(Principal principal,
                                       String goalName,
                                       String steps,
                                       String note,
                                       String emails)
    {
        AddGoalReturnParams addGoalReturnParams = new AddGoalReturnParams();

        HashMap<String, Object> params = new HashMap<>();

        int st = 0;

        try
        {
            st = Integer.parseInt(steps);

            if(st <= 0 || st > 625)
                throw new Exception();
        }
        catch(Exception e)
        {
            params.put("error", "error.wrongSteps");
            params.put("goalNameVal", goalName);
            params.put("noteVal", note);
            params.put("user", principal);

            addGoalReturnParams.setParams(params);
            addGoalReturnParams.setUrl("addgoal");
            return addGoalReturnParams;
        }

        if(goalName == null || goalName.trim().isEmpty())
        {
            params.put("error", "error.emptyGoal");
            params.put("stepsVal", steps);
            params.put("noteVal", note);
            params.put("user", principal);

            addGoalReturnParams.setParams(params);
            addGoalReturnParams.setUrl("addgoal");
            return addGoalReturnParams;
        }

        if(goalName.length() > 65535)
        {
            params.put("error", "error.tooLongGoalName");
            params.put("goalNameVal", goalName);
            params.put("stepsVal", steps);
            params.put("noteVal", note);
            params.put("user", principal);

            addGoalReturnParams.setParams(params);
            addGoalReturnParams.setUrl("addgoal");
            return addGoalReturnParams;
        }

        if(note.length() > 65535)
        {
            params.put("error", "error.tooLongNote");
            params.put("goalNameVal", goalName);
            params.put("stepsVal", steps);
            params.put("noteVal", note);
            params.put("user", principal);

            addGoalReturnParams.setParams(params);
            addGoalReturnParams.setUrl("addgoal");
            return addGoalReturnParams;
        }

        String goalTimestamp = new Timestamp(System.currentTimeMillis()).toString();

        UserAccounts userAccounts = userAccountsDAO.findByGoogleUsername(principal.getName());
        User user = userDAO.findByUserAccountsId(userAccounts.getId());

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
*/
		/*
		Only for group goals
		 */
        /*if(!emails.isEmpty() && emails != null)
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
                    params.put("error", "error.emailDoesNotExist");
                    params.put("user", user);

                    addGoalReturnParams.setParams(params);
                    addGoalReturnParams.setUrl("addgoal");
                    return addGoalReturnParams;
                }
            }
        }
        goalDAO.save(newGoal);

        addGoalReturnParams.setParams(params);
        addGoalReturnParams.setUrl("redirect:/goals");

        return addGoalReturnParams;
    }*/
}
