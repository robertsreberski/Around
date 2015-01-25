package com.StrapleGroup.around.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.base.AroundInfo;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.daos.AroundInfoDao;
import com.StrapleGroup.around.database.daos.FriendsInfoDao;
import com.StrapleGroup.around.database.intefaces.DataManager;
import com.StrapleGroup.around.database.tables.AroundInfoTable;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;

import java.util.List;

public class DataManagerImpl implements DataManager {


    private static final int DATABASE_VERSION = 1;

    private Context context;
    private SQLiteDatabase db;
    private FriendsInfoDao friendsDao;
    private AroundInfoDao aroundDao;
    private static DataManagerImpl mInstance = null;
//	private UserInfoDao userDao;

    public DataManagerImpl(Context context) {

        this.context = context;

        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();
        friendsDao = new FriendsInfoDao(db);
        aroundDao = new AroundInfoDao(db);

    }

    public static DataManagerImpl getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DataManagerImpl(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public FriendsInfo getFriendInfo(String name) {
        FriendsInfo pFriendInfo = friendsDao.get(findFriend(name));
        return pFriendInfo;
    }

    public AroundInfo getAroundInfo(String name) {
        return aroundDao.get(findAround(name));
    }

    @Override
    public Cursor getCompleteCursor() {
        return db.query(FriendsInfoTable.TABLE_NAME, new String[]{FriendsInfoTable.FriendsInfoColumns._ID,
                        FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND, FriendsInfoTable.FriendsInfoColumns.PROFILE_PHOTO, FriendsInfoTable.FriendsInfoColumns.X_FRIEND,
                        FriendsInfoTable.FriendsInfoColumns.Y_FRIEND, FriendsInfoTable.FriendsInfoColumns.STATUS, FriendsInfoTable.FriendsInfoColumns.ACTIVITY, FriendsInfoTable.FriendsInfoColumns.SCORE},
                null, null, null, null, FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND, null);
    }


    public Cursor getAroundCursor() {
        return db.query(AroundInfoTable.TABLE_NAME, new String[]{AroundInfoTable.AroundColumns._ID, AroundInfoTable.AroundColumns.LOGIN_FRIEND,
                AroundInfoTable.AroundColumns.X, AroundInfoTable.AroundColumns.Y,
                AroundInfoTable.AroundColumns.DISTANCE}, null, null, null, null, AroundInfoTable.AroundColumns.LOGIN_FRIEND + " COLLATE NOCASE ASC", null);
    }

    public List<AroundInfo> getAllAroundInfo() {
        return aroundDao.getAllAround();
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
    public void saveRequest(FriendsInfo friendsInfo) {
        try {
            db.beginTransaction();
            friendsDao.saveRequest(friendsInfo);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Transaction unsuccessful", "Broken DB");
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void updatePhoto(FriendsInfo friendsInfo) {
        try {
            db.beginTransaction();
            friendsDao.updatePhoto(friendsInfo);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Transaction unsuccessful", "Broken DB");
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void updateFriendInfo(FriendsInfo friendsInfo) {
        try {
            db.beginTransaction();
            friendsDao.update(friendsInfo);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Transaction unsuccessful", "Broken DB");
        } finally {
            db.endTransaction();
        }
    }

    public void updateAroundFriend(AroundInfo aroundInfo) {
        try {
            db.beginTransaction();
            aroundDao.update(aroundInfo);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Transaction unsuccessful", "Broken DB");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAroundList() {
        try {
            db.beginTransaction();
            aroundDao.deleteAll();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Transaction unsuccessful", "Broken DB");
        } finally {
            db.endTransaction();
        }
    }

    public void saveAroundFriend(AroundInfo aroundInfo) {
        try {
            db.beginTransaction();
            aroundDao.save(aroundInfo);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Transaction unsuccessful", "Broken DB");
        } finally {
            db.endTransaction();
        }
    }

    public long findAround(String login) {
        return aroundDao.find(login);
    }

    public void updateAround(AroundInfo aAround) {
        try {
            db.beginTransaction();
            aroundDao.update(aAround);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Transaction unsuccessful", "Broken DB");
        } finally {
            db.endTransaction();
        }
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

    public boolean deleteAround(long aroundId) {
        boolean result = false;
        try {
            db.beginTransaction();
            aroundDao.delete(Long.toString(aroundId));
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
