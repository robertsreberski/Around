package com.StrapleGroup.around.database.base;

public class FriendsInfo extends ModelBase {
	private String login_friend;
	private double x_friend;
	private double y_friend;
	
	
	
	
	public void setLoginFriend(String login_friend){
		this.login_friend = login_friend;
	}
	
	public String getLoginFriend(){
		return login_friend;
	}
	
	public void setXFriend(double x_friend){
		this.x_friend = x_friend;
	}
	
	public double getXFriend(){
		return x_friend;
	}
	
	public void setYFriend(double y_friend){
		this.y_friend = y_friend;
	}
	
	public double getYFriend(){
		return y_friend;
	}
	
}
