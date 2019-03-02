package com.pie.binarytable.entities;

import javax.persistence.*;

@Entity
@Table(name = "user_accounts")
public class UserAccounts
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String binaryTableName;

	private String binaryTableUsername;

	private String googleUsername;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getBinaryTableName()
	{
		return binaryTableName;
	}

	public void setBinaryTableName(String binaryTableName)
	{
		this.binaryTableName = binaryTableName;
	}

	public String getBinaryTableUsername()
	{
		return binaryTableUsername;
	}

	public void setBinaryTableUsername(String binaryTableUsername)
	{
		this.binaryTableUsername = binaryTableUsername;
	}

	public String getGoogleUsername()
	{
		return googleUsername;
	}

	public void setGoogleUsername(String googleUsername)
	{
		this.googleUsername = googleUsername;
	}
}
