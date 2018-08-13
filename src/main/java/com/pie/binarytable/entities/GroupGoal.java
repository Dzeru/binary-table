package com.pie.binarytable.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class GroupGoal
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	private Long goalId;

	@NotNull
	private Long userId;

	public GroupGoal(){}

	public GroupGoal(Long goalId, Long userId)
	{
		this.goalId = goalId;
		this.userId = userId;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Long getGoalId()
	{
		return goalId;
	}

	public void setGoalId(Long goalId)
	{
		this.goalId = goalId;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}
}
