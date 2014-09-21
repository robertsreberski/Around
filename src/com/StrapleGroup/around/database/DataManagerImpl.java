package com.StrapleGroup.around.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.daos.FriendsInfoDao;
import com.StrapleGroup.around.database.intefaces.DataManager;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;

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
    public Cursor getCompleteCursor() {
        return db.query(FriendsInfoTable.TABLE_NAME, new String[]{FriendsInfoTable.FriendsInfoColumns._ID,
                        FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND},
                null, null, null, null, FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND, null);
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
    public long saveLoginOnly(FriendsInfo friendsInfo) {
        long friendId = 0L;
        try {
            db.beginTransaction();
            friendId = friendsDao.saveLoginOnly(friendsInfo);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Transaction unsuccessful", "Can't save friend");
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
            friendsDao.delete(Long.toString(friendId));
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
