package com.pie.binarytable.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "goal")
public class Goal
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	private String goalName;

	@NotNull
	private int allSteps;

	@NotNull
	private int doneSteps;

	@NotNull
	private String currentState;

	private String note;

	@NotNull
	private Long userId;

	@NotNull
	private boolean isFinished;

	@NotNull
	private boolean isGroupGoal;

	@NotNull
	private String goalTimestamp;

/*
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
*/

	public Goal(){}

	public Goal(Long userId, String goalName, int allSteps, String goalTimestamp)
	{
		this.goalName = goalName;
		this.allSteps = allSteps;
		this.userId = userId;
		this.goalTimestamp = goalTimestamp;
	}

	public Goal(Long userId, String goalName, int allSteps, String note, String goalTimestamp)
	{	this.goalName = goalName;
		this.allSteps = allSteps;
		this.note = note;
		this.userId = userId;
		this.goalTimestamp = goalTimestamp;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getGoalName()
	{
		return goalName;
	}

	public void setGoalName(String goalName)
	{
		this.goalName = goalName;
	}

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

	public String getCurrentState()
	{
		return currentState;
	}

	public void setCurrentState(String currentState)
	{
		this.currentState = currentState;
	}

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

	public boolean isFinished()
	{
		return isFinished;
	}

	public void setFinished(boolean finished)
	{
		isFinished = finished;
	}

	public boolean isGroupGoal()
	{
		return isGroupGoal;
	}

	public void setGroupGoal(boolean groupGoal)
	{
		isGroupGoal = groupGoal;
	}

	public String getGoalTimestamp() {
		return goalTimestamp;
	}

	public void setGoalTimestamp(String goalTimestamp) {
		this.goalTimestamp = goalTimestamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Goal goal = (Goal) o;
		return getAllSteps() == goal.getAllSteps() &&
				getDoneSteps() == goal.getDoneSteps() &&
				isFinished() == goal.isFinished() &&
				isGroupGoal() == goal.isGroupGoal() &&
				Objects.equals(getId(), goal.getId()) &&
				Objects.equals(getGoalName(), goal.getGoalName()) &&
				Objects.equals(getCurrentState(), goal.getCurrentState()) &&
				Objects.equals(getNote(), goal.getNote()) &&
				Objects.equals(getUserId(), goal.getUserId()) &&
				Objects.equals(getGoalTimestamp(), goal.getGoalTimestamp());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getGoalName(), getAllSteps(), getDoneSteps(), getCurrentState(), getNote(), getUserId(), isFinished(), isGroupGoal(), getGoalTimestamp());
	}
}
