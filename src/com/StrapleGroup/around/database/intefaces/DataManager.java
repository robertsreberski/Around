package com.StrapleGroup.around.database.intefaces;

import android.database.Cursor;

import com.StrapleGroup.around.database.base.FriendsInfo;

import java.util.List;

public interface DataManager {

    public FriendsInfo getFriendInfo(long friendId);

    public List<FriendsInfo> getAllFriendsInfo();

    long findFriend(String friendLogin);

    public long saveFriendInfo(FriendsInfo friendInfo);

    void saveRequest(FriendsInfo friendsInfo);

    void updatePhoto(FriendsInfo friendsInfo);

    void updateFriendInfo(FriendsInfo friendsInfo);

    Cursor getCompleteCursor();

    public boolean deleteFriend(long friendId);

}
