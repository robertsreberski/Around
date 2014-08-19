package com.StrapleGroup.around.database.intefaces;

import java.util.List;

import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.base.UserInfo;

public interface DataManager {
	
	public FriendsInfo getFriendInfo(long friendId);
	public List<FriendsInfo> getAllFriendsInfo();
	public FriendsInfo findFriendInfo(String friendLogin);
	public long saveFriendInfo(FriendsInfo friendInfo);
	public boolean deleteFriend(long friendId);
}
