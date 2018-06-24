package com.pie.binarytable.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "goal")
public class Goal
{
	private Long id;
	private String goalName;
	private int allSteps;
	private int doneSteps;
	private String currentState;
	private String note;
	private Long userId;

/*
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
*/

	public Goal(){}

	public Goal(Long userId, String goalName, int allSteps)
	{
		this.goalName = goalName;
		this.allSteps = allSteps;
		this.userId = userId;
	}

	public Goal(Long userId, String goalName, int allSteps, String note)
	{	this.goalName = goalName;
		this.allSteps = allSteps;
		this.note = note;
		this.userId = userId;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	@Basic
	@Column(name = "goal_name", nullable = false, length = 100)
	public String getGoalName()
	{
		return goalName;
	}

	public void setGoalName(String goalName)
	{
		this.goalName = goalName;
	}

	@Basic
	@Column(name = "all_steps", nullable = false)
	public int getAllSteps()
	{
		return allSteps;
	}

	public void setAllSteps(int allSteps)
	{
		this.allSteps = allSteps;
	}

	public int getDoneSteps()
	{
		return doneSteps;
	}

	public void setDoneSteps(int doneSteps)
	{
		this.doneSteps = doneSteps;
	}

	@Basic
	@Column(name = "current_state", nullable = false, length = 1000)
	public String getCurrentState()
	{
		return currentState;
	}

	public void setCurrentState(String currentState)
	{
		this.currentState = currentState;
	}

	@Basic
	@Column(name = "note", nullable = true, length = 1000)
	public String getNote()
	{
		return note;
	}

	public void setNote(String note)
	{
		this.note = note;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof Goal)) return false;
		Goal goal = (Goal) o;
		return getAllSteps() == goal.getAllSteps() &&
				getDoneSteps() == goal.getDoneSteps() &&
				Objects.equals(getId(), goal.getId()) &&
				Objects.equals(getGoalName(), goal.getGoalName()) &&
				Objects.equals(getCurrentState(), goal.getCurrentState()) &&
				Objects.equals(getNote(), goal.getNote()) &&
				Objects.equals(getUserId(), goal.getUserId());
	}

	@Override
	public int hashCode()
	{

		return Objects.hash(getId(), getGoalName(), getAllSteps(), getDoneSteps(), getCurrentState(), getNote(), getUserId());
	}

	/*
	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof Goal)) return false;
		Goal goal = (Goal) o;
		return getId() == goal.getId() &&
				getAllSteps() == goal.getAllSteps() &&
				doneSteps == goal.doneSteps &&
				Objects.equals(getGoalName(), goal.getGoalName()) &&
				Objects.equals(getCurrentState(), goal.getCurrentState()) &&
				Objects.equals(getNote(), goal.getNote()) &&
				Objects.equals(getUser(), goal.getUser());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getId(), getGoalName(), getAllSteps(), doneSteps, getCurrentState(), getNote(), getUser());
	}*/
}
