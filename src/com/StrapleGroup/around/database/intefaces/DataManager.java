package com.StrapleGroup.around.database.intefaces;

import java.util.List;

import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.base.UserInfo;

public interface DataManager {
	
	public long saveUserInfo(UserInfo userInfo);
	public void updateUserCoordinates(UserInfo userInfo);
	public boolean deleteUserInfo(long userId);
	public List<UserInfo> getAllUserInfo();
	public List<UserInfo> getLogin();
	
	public FriendsInfo getFriendInfo(long friendId);
	public List<FriendsInfo> getAllFriendsInfo();
	public FriendsInfo findFriendInfo(String friendLogin);
	public long saveFriendInfo(FriendsInfo friendInfo);
	public boolean deleteFriend(long friendId);
}
