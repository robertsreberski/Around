package com.StrapleGroup.nearby.database.base;

public class FriendsInfo extends ModelBase {
	private String login_friend;
	private double x_friend;
	private double y_friend;
	
	
	
	
	private void setLoginFriend(String login_friend){
		this.login_friend = login_friend;
	}
	
	private String getLoginFriend(){
		return login_friend;
	}
	
	private void setXFriend(double x_friend){
		this.x_friend = x_friend;
	}
	
	private double getXFriend(){
		return x_friend;
	}
	
	private void setYFriend(double y_friend){
		this.y_friend = y_friend;
	}
	
	private double getYFriend(){
		return y_friend;
	}
	
}
