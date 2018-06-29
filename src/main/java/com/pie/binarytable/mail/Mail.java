package com.pie.binarytable.mail;

public class Mail
{
	String text;
	
	/*
	1st %s is Name of user, not username!, 2nd is link, 3rd is e-mail of BiT
	*/
	static String templateForForgotPassword = "Hello, %s! You tap \"Forgot the password?\" in the Binary Table. Click %s to renew your password. If it wasn't you who tapped, ignore this letter or write us to %s, if this action with your account was strange.";
	
	static String accountActivation = "Hello, %s! You have sign in to Binary Table. Your username: %s Your password: %s. Keep it in the safe place! We wish you reachable goals!";
	
	public Mail(){}
}

