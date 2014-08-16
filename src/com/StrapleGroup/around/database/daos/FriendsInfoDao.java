package com.StrapleGroup.around.database.daos;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.intefaces.Dao;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;
import com.StrapleGroup.around.database.tables.FriendsInfoTable.FriendsInfoColumns;

public class FriendsInfoDao implements Dao<FriendsInfo> {
	private static final String INSERT_ALL = "insert into "
			+ FriendsInfoTable.TABLE_NAME + "("
			+ FriendsInfoColumns.LOGIN_FRIEND + " , "
			+ FriendsInfoColumns.X_FRIEND + " , " + FriendsInfoColumns.Y_FRIEND
			+ ") values (?, ?, ?)";
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
		insertStatement.bindDouble(2, friendsInfo.getXFriend());
		insertStatement.bindDouble(3, friendsInfo.getYFriend());
		return insertStatement.executeInsert();
	}

	@Override
	public void update(FriendsInfo friendsInfo) {
		final ContentValues values = new ContentValues();
		values.put(FriendsInfoColumns.LOGIN_FRIEND, friendsInfo.getLoginFriend());
		values.put(FriendsInfoColumns.X_FRIEND, friendsInfo.getXFriend());
		values.put(FriendsInfoColumns.Y_FRIEND, friendsInfo.getYFriend());
		db.update(FriendsInfoTable.TABLE_NAME, values,
				BaseColumns._ID + " = ?",
				new String[] { String.valueOf(friendsInfo.getId()) });
	}
	@Override
	public void updateCoordinates(FriendsInfo friendsInfo){
		final ContentValues pValues = new ContentValues();
		pValues.put(FriendsInfoColumns.X_FRIEND, friendsInfo.getXFriend());
		pValues.put(FriendsInfoColumns.Y_FRIEND, friendsInfo.getYFriend());
		db.update(FriendsInfoTable.TABLE_NAME, pValues , BaseColumns._ID + " =?", new String[] {String.valueOf(friendsInfo.getId())});
	}

	@Override
	public void delete(FriendsInfo friendsInfo) {
		if (friendsInfo.getId() > 0) {
			db.delete(FriendsInfoTable.TABLE_NAME, BaseColumns._ID + " =?",
					new String[] { String.valueOf(friendsInfo.getId()) });
		}
	}

	public FriendsInfo get(long id) {
		FriendsInfo pFriendsInfo = null;
		Cursor pCursor = db.query(FriendsInfoTable.TABLE_NAME, new String[] {
				BaseColumns._ID, FriendsInfoColumns.LOGIN_FRIEND,
				FriendsInfoColumns.X_FRIEND, FriendsInfoColumns.Y_FRIEND },
				BaseColumns._ID + " =?", new String[] { String.valueOf(id) },
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
		Cursor pCursor = db.query(FriendsInfoTable.TABLE_NAME, new String[] {
				BaseColumns._ID, FriendsInfoColumns.LOGIN_FRIEND,
				FriendsInfoColumns.X_FRIEND, FriendsInfoColumns.Y_FRIEND },
				null, null, null, null, FriendsInfoColumns.LOGIN_FRIEND, null);
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
			pFriendsInfo.setXFriend(pCursor.getDouble(2));
			pFriendsInfo.setYFriend(pCursor.getDouble(3));
		}
		return pFriendsInfo;
	}
	
	public FriendsInfo find(String login){
		long pFriendId = 0L;
		String sql = "select _id from " + FriendsInfoTable.TABLE_NAME + " where upper(" + FriendsInfoColumns.LOGIN_FRIEND + ") = ? limit 1";
		Cursor pCursor = db.rawQuery(sql, new String[] { login.toUpperCase()});
		if(pCursor.moveToFirst()){
			pFriendId = pCursor.getLong(0);
		}
		if(!pCursor.isClosed()){
			pCursor.close();
		}
		return this.get(pFriendId);
	}
}
