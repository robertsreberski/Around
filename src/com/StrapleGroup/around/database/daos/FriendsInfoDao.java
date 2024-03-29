package com.StrapleGroup.around.database.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.intefaces.Dao;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;
import com.StrapleGroup.around.database.tables.FriendsInfoTable.FriendsInfoColumns;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;

public class FriendsInfoDao implements Dao<FriendsInfo> {
    private static final String INSERT_ALL = "insert into "
            + FriendsInfoTable.TABLE_NAME + "("
            + FriendsInfoColumns.LOGIN_FRIEND + " , "
            + FriendsInfoColumns.PROFILE_PHOTO + " , "
            + FriendsInfoColumns.X_FRIEND + " , " + FriendsInfoColumns.Y_FRIEND + " , "
            + FriendsInfoColumns.STATUS + " , " + FriendsInfoColumns.ACTIVITY + " , "
            + FriendsInfoColumns.SCORE + " , " + FriendsInfoColumns.IS_POKED
            + ") values (?, ?, ?, ?, ?, ?, ?, ?)";
    private SQLiteDatabase db;
    private SQLiteStatement insertStatement;

    public FriendsInfoDao(SQLiteDatabase db) {
        this.db = db;
        insertStatement = db.compileStatement(INSERT_ALL);
    }

    @Override
    public long save(FriendsInfo friendsInfo) {
        insertStatement.clearBindings();
        insertStatement.bindString(1, friendsInfo.getLoginFriend());
        insertStatement.bindBlob(2, friendsInfo.getProfilePhoto());
        insertStatement.bindDouble(3, friendsInfo.getXFriend());
        insertStatement.bindDouble(4, friendsInfo.getYFriend());
        insertStatement.bindString(5, friendsInfo.getStatus());
        insertStatement.bindDouble(6, friendsInfo.getActivities());
        insertStatement.bindDouble(7, friendsInfo.getScore());
        insertStatement.bindString(8, "false");
        return insertStatement.executeInsert();
    }

    @Override
    public void saveRequest(FriendsInfo friendsInfo) {
        insertStatement.clearBindings();
        insertStatement.bindString(1, friendsInfo.getLoginFriend());
        insertStatement.bindBlob(2, friendsInfo.getProfilePhoto());
        insertStatement.bindDouble(3, 0);
        insertStatement.bindDouble(4, 0);
        insertStatement.bindString(5, friendsInfo.getStatus());
        insertStatement.bindDouble(6, DetectedActivity.UNKNOWN);
        insertStatement.bindDouble(7, friendsInfo.getScore());
        insertStatement.bindString(8, "false");
        insertStatement.executeInsert();
    }

    @Override
    public void update(FriendsInfo friendsInfo) {
        final ContentValues values = new ContentValues();
        values.put(FriendsInfoColumns.LOGIN_FRIEND, friendsInfo.getLoginFriend());
        values.put(FriendsInfoColumns.X_FRIEND, friendsInfo.getXFriend());
        values.put(FriendsInfoColumns.Y_FRIEND, friendsInfo.getYFriend());
        values.put(FriendsInfoColumns.STATUS, friendsInfo.getStatus());
        values.put(FriendsInfoColumns.ACTIVITY, friendsInfo.getActivities());
        values.put(FriendsInfoColumns.SCORE, friendsInfo.getScore());
        db.update(FriendsInfoTable.TABLE_NAME, values,
                BaseColumns._ID + " = " + find(friendsInfo.getLoginFriend()), null);
    }

    @Override
    public void updatePhoto(FriendsInfo friendsInfo) {
        final ContentValues values = new ContentValues();
        values.put(FriendsInfoColumns.LOGIN_FRIEND, friendsInfo.getLoginFriend());
        values.put(FriendsInfoColumns.PROFILE_PHOTO, friendsInfo.getProfilePhoto());
        db.update(FriendsInfoTable.TABLE_NAME, values,
                BaseColumns._ID + " = " + find(friendsInfo.getLoginFriend()), null);
    }

    public void setPoked(FriendsInfo friendsInfo) {
        final ContentValues values = new ContentValues();
        values.put(FriendsInfoColumns.LOGIN_FRIEND, friendsInfo.getLoginFriend());
        values.put(FriendsInfoColumns.IS_POKED, friendsInfo.isPoked());
        db.update(FriendsInfoTable.TABLE_NAME, values,
                BaseColumns._ID + " = " + find(friendsInfo.getLoginFriend()), null);
    }

    @Override
    public void delete(String id) {
        db.delete(FriendsInfoTable.TABLE_NAME, BaseColumns._ID + " =" + id, null);
    }


    public FriendsInfo get(long id) {
        FriendsInfo pFriendsInfo = null;
        Cursor pCursor = db.query(FriendsInfoTable.TABLE_NAME, new String[]{FriendsInfoTable.FriendsInfoColumns._ID,
                        FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND, FriendsInfoTable.FriendsInfoColumns.PROFILE_PHOTO, FriendsInfoTable.FriendsInfoColumns.X_FRIEND,
                        FriendsInfoTable.FriendsInfoColumns.Y_FRIEND, FriendsInfoTable.FriendsInfoColumns.STATUS, FriendsInfoTable.FriendsInfoColumns.ACTIVITY,
                        FriendsInfoColumns.SCORE, FriendsInfoColumns.IS_POKED},
                BaseColumns._ID + " =?", new String[]{String.valueOf(id)},
                null, null, null, "1");
        if (pCursor.moveToFirst()) {
            pFriendsInfo = this.buildUserInfoFromCursor(pCursor);
        }
        if (!pCursor.isClosed()) {
            pCursor.close();
        }
        return pFriendsInfo;
    }

    @Override
    public List<FriendsInfo> getAll() {
        List<FriendsInfo> pFriendsList = new ArrayList<FriendsInfo>();
        Cursor pCursor = db.query(FriendsInfoTable.TABLE_NAME, new String[]{FriendsInfoTable.FriendsInfoColumns._ID,
                        FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND, FriendsInfoTable.FriendsInfoColumns.PROFILE_PHOTO, FriendsInfoTable.FriendsInfoColumns.X_FRIEND,
                        FriendsInfoTable.FriendsInfoColumns.Y_FRIEND, FriendsInfoTable.FriendsInfoColumns.STATUS, FriendsInfoTable.FriendsInfoColumns.ACTIVITY,
                        FriendsInfoColumns.SCORE, FriendsInfoColumns.IS_POKED},
                null, null, null, null, FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND + " COLLATE NOCASE ASC", null);
        if (pCursor.moveToFirst()) {
            do {
                FriendsInfo pFriendsInfo = this
                        .buildUserInfoFromCursor(pCursor);
                if (pFriendsInfo != null) {
                    pFriendsList.add(pFriendsInfo);
                }
            } while (pCursor.moveToNext());
        }
        if (!pCursor.isClosed()) {
            pCursor.close();
        }
        return pFriendsList;
    }

    private FriendsInfo buildUserInfoFromCursor(Cursor pCursor) {
        FriendsInfo pFriendsInfo = null;
        if (pCursor != null) {
            pFriendsInfo = new FriendsInfo();
            pFriendsInfo.setId(pCursor.getLong(0));
            pFriendsInfo.setLoginFriend(pCursor.getString(1));
            pFriendsInfo.setProfilePhoto(pCursor.getBlob(2));
            pFriendsInfo.setXFriend(pCursor.getDouble(3));
            pFriendsInfo.setYFriend(pCursor.getDouble(4));
            pFriendsInfo.setStatus(pCursor.getString(5));
            pFriendsInfo.setActivities(pCursor.getInt(6));
            pFriendsInfo.setScore(pCursor.getInt(7));
            pFriendsInfo.setPoked(Boolean.parseBoolean(pCursor.getString(8)));
        }
        return pFriendsInfo;
    }

    public long find(String login) {
        long pFriendId = -1;
        String sql = "select _id from " + FriendsInfoTable.TABLE_NAME + " where upper(" + FriendsInfoColumns.LOGIN_FRIEND + ") = ? limit 1";
        Cursor pCursor = db.rawQuery(sql, new String[]{login.toUpperCase()});
        if (pCursor.moveToFirst()) {
            pFriendId = pCursor.getLong(0);
        }
        if (!pCursor.isClosed()) {
            pCursor.close();
        }
        return pFriendId;
    }
}
