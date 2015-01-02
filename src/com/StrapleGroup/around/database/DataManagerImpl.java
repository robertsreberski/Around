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
import com.StrapleGroup.around.database.base.LogInfo;
import com.StrapleGroup.around.database.daos.AroundInfoDao;
import com.StrapleGroup.around.database.daos.FriendsInfoDao;
import com.StrapleGroup.around.database.daos.LogsDao;
import com.StrapleGroup.around.database.intefaces.DataManager;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;

import java.util.List;

public class DataManagerImpl implements DataManager {


    private static final int DATABASE_VERSION = 1;

    private Context context;
    private SQLiteDatabase db;
    private FriendsInfoDao friendsDao;
    private LogsDao logsDao;
    private AroundInfoDao aroundDao;
//	private UserInfoDao userDao;

    public DataManagerImpl(Context context) {

        this.context = context;

        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();
        friendsDao = new FriendsInfoDao(db);
        logsDao = new LogsDao(db);
        aroundDao = new AroundInfoDao(db);

    }

    @Override
    public FriendsInfo getFriendInfo(long friendId) {
        FriendsInfo pFriendInfo = friendsDao.get(friendId);
        return pFriendInfo;
    }

    @Override
    public Cursor getCompleteCursor() {
        return db.query(FriendsInfoTable.TABLE_NAME, new String[]{FriendsInfoTable.FriendsInfoColumns._ID,
                        FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND, FriendsInfoTable.FriendsInfoColumns.PROFILE_PHOTO, FriendsInfoTable.FriendsInfoColumns.X_FRIEND,
                        FriendsInfoTable.FriendsInfoColumns.Y_FRIEND, FriendsInfoTable.FriendsInfoColumns.STATUS, FriendsInfoTable.FriendsInfoColumns.ACTIVITY},
                null, null, null, null, FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND, null);
    }

    public Cursor getAroundCursor() {
        return db.query(FriendsInfoTable.TABLE_NAME, new String[]{FriendsInfoTable.FriendsInfoColumns._ID,
                        FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND, FriendsInfoTable.FriendsInfoColumns.PROFILE_PHOTO, FriendsInfoTable.FriendsInfoColumns.X_FRIEND,
                        FriendsInfoTable.FriendsInfoColumns.Y_FRIEND, FriendsInfoTable.FriendsInfoColumns.STATUS, FriendsInfoTable.FriendsInfoColumns.ACTIVITY},
                FriendsInfoTable.FriendsInfoColumns.STATUS + " =?", new String[]{Constants.STATUS_ONLINE}, null, null, FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND, null);
    }

    @Override
    public List<FriendsInfo> getAllFriendsInfo() {
        return friendsDao.getAll();
    }

    @Override
    public long findFriend(String friendLogin) {
        return friendsDao.find(friendLogin);
    }

    public void saveLog(LogInfo logInfo) {
        try {
            db.beginTransaction();
            logsDao.save(logInfo);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("Transaction unsuccessful", "SthBroke");
        } finally {
            db.endTransaction();
        }
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
//    @Override
//    public long saveLoginOnly(FriendsInfo friendsInfo) {
//        long friendId = 0L;
//        try {
//            db.beginTransaction();
//            friendId = friendsDao.saveLoginOnly(friendsInfo);
//            db.setTransactionSuccessful();
//        } catch (SQLException e) {
//            Log.e("Transaction unsuccessful", "Can't save friend");
//            friendId = 0L;
//        } finally {
//            db.endTransaction();
//        }
//        return friendId;
//    }


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
