package com.StrapleGroup.around.database.base;


public class UserInfo extends ModelBase{
	private String login;
	private String pass;
	private double x;
	private double y;
	
	public UserInfo(){
	}
	
	public void setLogin(String login){
		this.login = login;
	}
	
	public String getLogin(){
		return login;
	}
	
	public void setPass(String pass){
		this.pass = pass;
	}
	
	public String getPass(){
		return pass;
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public double getX(){
		return x;
	}
	
	public void setY(double y){
		this.y = y;
	}
	public double getY(){
		return y;
	}
	
}
