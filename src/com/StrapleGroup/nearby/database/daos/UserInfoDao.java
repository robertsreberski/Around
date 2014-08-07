package com.StrapleGroup.nearby.database.daos;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.StrapleGroup.nearby.database.base.UserInfo;
import com.StrapleGroup.nearby.database.intefaces.Dao;
import com.StrapleGroup.nearby.database.tables.UserInfoTable;
import com.StrapleGroup.nearby.database.tables.UserInfoTable.UserInfoColumns;

public class UserInfoDao implements Dao<UserInfo> {
	
	private static final String INSERT_ALL = "insert into " + UserInfoTable.TABLE_NAME + "("
	+ UserInfoColumns.LOGIN + " , " + UserInfoColumns.PASS + " , "
			+ UserInfoColumns.X + " , " + UserInfoColumns.Y
			+ ") values (?, ?, ?, ?)";
	
	private SQLiteDatabase db;
	private SQLiteStatement insertStatement;
	
	public UserInfoDao(SQLiteDatabase db){
		this.db =db;
		insertStatement = db.compileStatement(INSERT_ALL);
	}
	
	@Override
	public long save(UserInfo entity) {
		insertStatement.clearBindings();
		insertStatement.bindString(1, entity.getLogin());
		insertStatement.bindString(2, entity.getPass());
		insertStatement.bindDouble(3, entity.getX());
		insertStatement.bindDouble(4, entity.getY());
		return insertStatement.executeInsert();
	}

	@Override
	public void update(UserInfo entity) {
		final ContentValues values = new ContentValues();
		values.put(UserInfoColumns.LOGIN, entity.getLogin());
		values.put(UserInfoColumns.PASS, entity.getPass());
		values.put(UserInfoColumns.X, entity.getX());
		values.put(UserInfoColumns.Y, entity.getY());
		db.update(UserInfoTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[] {String.valueOf(entity.getId()) });
	}

	@Override
	public void delete(UserInfo entity) {
		if ( entity.getId() > 0) {
			db.delete(UserInfoTable.TABLE_NAME, BaseColumns._ID + " =?", new String[] { String.valueOf(entity.getId()) });
		}
	}

	@Override
	public UserInfo get(long id) {
		UserInfo pUserInfo = null;
		Cursor pCursor = db.query(UserInfoTable.TABLE_NAME, new String[]{ BaseColumns._ID, UserInfoColumns.LOGIN, UserInfoColumns.PASS, UserInfoColumns.X, UserInfoColumns.Y }, 
				BaseColumns._ID + " =?" , new String[] { String.valueOf(id) }, null, null, null, "1" );
		if(pCursor.moveToFirst()){
			pUserInfo = this.buildUserInfoFromCursor(pCursor);
		}
		if(!pCursor.isClosed()){
			pCursor.close();
		}
		return pUserInfo;
	}

	@Override
	public List<UserInfo> getAll() {
		List<UserInfo> pUserList = new ArrayList<UserInfo>();
		Cursor pCursor = db.query(UserInfoTable.TABLE_NAME, new String[] { BaseColumns._ID, UserInfoColumns.LOGIN, UserInfoColumns.PASS, UserInfoColumns.X, UserInfoColumns.Y },
				null, null, null, null, UserInfoColumns.LOGIN, null);
		if(pCursor.moveToFirst()){
			do {
				UserInfo pUserInfo = this.buildUserInfoFromCursor(pCursor);
				if(pUserInfo != null){
					pUserList.add(pUserInfo);
				} 
			} while(pCursor.moveToNext());
		}
		if (!pCursor.isClosed()){
			pCursor.close();
		}
		return pUserList;
	}
	
//	@SuppressWarnings("null")
	private UserInfo buildUserInfoFromCursor(Cursor pCursor){
		UserInfo pUserInfo = null;
		if(pCursor != null){
			pUserInfo.setId(pCursor.getLong(0));
			pUserInfo.setLogin(pCursor.getString(1));
			pUserInfo.setPass(pCursor.getString(2));
			pUserInfo.setX(pCursor.getDouble(3));
			pUserInfo.setY(pCursor.getDouble(4));
		}
		return pUserInfo;
	}

}
