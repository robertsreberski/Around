package com.StrapleGroup.around.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.daos.FriendsInfoDao;
import com.StrapleGroup.around.database.intefaces.DataManager;

import java.util.List;

public class DataManagerImpl implements DataManager {


    private static final int DATABASE_VERSION = 1;

    private Context context;
    private SQLiteDatabase db;
    private FriendsInfoDao friendsDao;
//	private UserInfoDao userDao;

    public DataManagerImpl(Context context) {

        this.context = context;

        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();
        friendsDao = new FriendsInfoDao(db);
    }

    @Override
    public FriendsInfo getFriendInfo(long friendId) {
        FriendsInfo pFriendInfo = friendsDao.get(friendId);
        return pFriendInfo;
    }

    @Override
    public List<FriendsInfo> getAllFriendsInfo() {
        return friendsDao.getAll();
    }

    @Override
    public long findFriend(String friendLogin) {
        return friendsDao.find(friendLogin);
    }

    @Override
    public long saveFriendInfo(FriendsInfo friendInfo) {
        long friendId = 0L;
        try {
            db.beginTransaction();
            friendId = friendsDao.save(friendInfo);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Transaction unsuccessful", "SthBroke");
            friendId = 0L;
        } finally {
            db.endTransaction();
        }
        return friendId;
    }

    @Override
    public boolean deleteFriend(long friendId) {
        boolean result = false;
        try {
            db.beginTransaction();
            FriendsInfo friendInfo = friendsDao.get(friendId);
            friendsDao.delete(friendInfo);
            db.setTransactionSuccessful();
            result = true;
        } catch (SQLException e) {
            Log.e("Transaction unsuccessful", "SthBroke");
        } finally {
            db.endTransaction();
        }
        return result;
    }


}
