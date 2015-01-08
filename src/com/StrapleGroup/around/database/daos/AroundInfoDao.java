package com.StrapleGroup.around.database.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.StrapleGroup.around.database.base.AroundInfo;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.tables.AroundInfoTable;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;

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
        values.put(AroundInfoTable.AroundColumns.LOGIN_FRIEND, aroundInfo.getLogin());
        values.put(AroundInfoTable.AroundColumns.DISTANCE, aroundInfo.getDistance());
        db.update(FriendsInfoTable.TABLE_NAME, values,
                BaseColumns._ID + " = " + find(aroundInfo.getLogin()), null);
    }

    public AroundInfo get(long id) {
        AroundInfo aroundInfo = null;
        Cursor pCursor = db.query(AroundInfoTable.TABLE_NAME, new String[]{AroundInfoTable.AroundColumns.LOGIN_FRIEND, AroundInfoTable.AroundColumns.DISTANCE},
                BaseColumns._ID + " =?", new String[]{String.valueOf(id)},
                null, null, null, "1");
        if (pCursor.moveToFirst()) {
            aroundInfo = this.buildUserInfoFromCursor(pCursor);
        }
        if (!pCursor.isClosed()) {
            pCursor.close();
        }
        return aroundInfo;
    }

    public void delete(String id) {
        db.delete(AroundInfoTable.TABLE_NAME, BaseColumns._ID + " =" + id, null);
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

    private AroundInfo buildUserInfoFromCursor(Cursor pCursor) {
        AroundInfo pAroundInfo = null;
        if (pCursor != null) {
            pAroundInfo = new AroundInfo();
            pAroundInfo.setId(pCursor.getLong(0));
            pAroundInfo.setLogin(pCursor.getString(1));
            pAroundInfo.setDistance(Float.toString(pCursor.getFloat(2)));
        }
        return pAroundInfo;
    }

}
