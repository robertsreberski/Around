package com.StrapleGroup.around.database.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.StrapleGroup.around.database.base.AroundInfo;
import com.StrapleGroup.around.database.tables.AroundInfoTable;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;
import com.StrapleGroup.around.database.tables.LogsTable;

/**
 * Created by Robert on 2014-12-29.
 */
public class AroundInfoDao {
    private static final String INSERT_ALL = "insert into "
            + AroundInfoTable.TABLE_NAME + "("
            + AroundInfoTable.AroundColumns.LOGIN_FRIEND + " , "
            + AroundInfoTable.AroundColumns.DISTANCE
            + ") values (?, ?)";
    private SQLiteDatabase db;
    private SQLiteStatement insertStatement;

    public AroundInfoDao(SQLiteDatabase db) {
        this.db = db;
        insertStatement = db.compileStatement(INSERT_ALL);
    }

    public void save(AroundInfo aroundInfo) {
        insertStatement.clearBindings();
        insertStatement.bindString(1, aroundInfo.getLogin());
        insertStatement.bindString(2, aroundInfo.getDistance());
        insertStatement.executeInsert();
    }

    public void deleteAll() {
        db.delete(AroundInfoTable.TABLE_NAME, null, null);
    }

    public void update(AroundInfo aroundInfo) {
        final ContentValues values = new ContentValues();
        values.put(FriendsInfoTable.FriendsInfoColumns.LOGIN_FRIEND, aroundInfo.getLogin());
        values.put(FriendsInfoTable.FriendsInfoColumns.PROFILE_PHOTO, aroundInfo.getDistance());
        db.update(FriendsInfoTable.TABLE_NAME, values,
                BaseColumns._ID + " = " + find(aroundInfo.getLogin()), null);
    }

    public long find(String login) {
        long pFriendId = -1;
        String sql = "select _id from " + AroundInfoTable.TABLE_NAME + " where upper(" + AroundInfoTable.AroundColumns.LOGIN_FRIEND + ") = ? limit 1";
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
