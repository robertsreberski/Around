package com.StrapleGroup.around.database.intefaces;

import com.StrapleGroup.around.database.base.FriendsInfo;

import java.util.List;

public interface DataManager {

    public FriendsInfo getFriendInfo(long friendId);

    public List<FriendsInfo> getAllFriendsInfo();

    long findFriend(String friendLogin);

    public long saveFriendInfo(FriendsInfo friendInfo);

    public boolean deleteFriend(long friendId);

}
