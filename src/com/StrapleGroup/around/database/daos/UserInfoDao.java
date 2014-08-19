package com.StrapleGroup.around.database.daos;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteStatement;
//import android.provider.BaseColumns;
//
//import com.StrapleGroup.around.database.base.UserInfo;
//import com.StrapleGroup.around.database.intefaces.Dao;
//import com.StrapleGroup.around.database.tables.UserInfoTable;
//import com.StrapleGroup.around.database.tables.UserInfoTable.UserInfoColumns;
//
//public class UserInfoDao implements Dao<UserInfo> {
//
//	private static final String INSERT_ALL = "insert into "
//			+ UserInfoTable.TABLE_NAME + "(" + UserInfoColumns.LOGIN + " , "
//			+ UserInfoColumns.PASS + " , " + UserInfoColumns.X + " , "
//			+ UserInfoColumns.Y + ") values (?, ?, ?, ?)";
//
//	private SQLiteDatabase db;
//	private SQLiteStatement insertStatement;
//
//	public UserInfoDao(SQLiteDatabase db) {
//		this.db = db;
//		insertStatement = db.compileStatement(INSERT_ALL);
//	}
//
//	@Override
//	public long save(UserInfo userInfo) {
//		insertStatement.clearBindings();
//		insertStatement.bindString(1, userInfo.getLogin());
//		insertStatement.bindString(2, userInfo.getPass());
//		insertStatement.bindDouble(3, userInfo.getX());
//		insertStatement.bindDouble(4, userInfo.getY());
//		return insertStatement.executeInsert();
//	}
//
//	@Override
//	public void updateCoordinates(UserInfo userInfo) {
//		final ContentValues pValues = new ContentValues();
//		pValues.put(UserInfoColumns.X, userInfo.getX());
//		pValues.put(UserInfoColumns.Y, userInfo.getY());
//		db.update(UserInfoTable.TABLE_NAME, pValues, null, new String[] { "2",
//				"3" });
//	}
//
//	@Override
//	public void update(UserInfo userInfo) {
//		final ContentValues pValues = new ContentValues();
//		pValues.put(UserInfoColumns.LOGIN, userInfo.getLogin());
//		pValues.put(UserInfoColumns.PASS, userInfo.getPass());
//		pValues.put(UserInfoColumns.X, userInfo.getX());
//		pValues.put(UserInfoColumns.Y, userInfo.getY());
//		db.update(UserInfoTable.TABLE_NAME, pValues, null,
//				new String[] { String.valueOf(userInfo.getId()) });
//	}
//
//	@Override
//	public void delete(UserInfo userInfo) {
//		if (userInfo.getId() > 0) {
//			db.delete(UserInfoTable.TABLE_NAME, null,
//					new String[] { String.valueOf(userInfo.getId()) });
//		}
//	}
//
//	public List<UserInfo> getLogin() {
//		List<UserInfo> pLogin = new ArrayList<UserInfo>();
//		Cursor pCursor = db.query(UserInfoTable.TABLE_NAME,
//				new String[] { UserInfoColumns.LOGIN }, null, null, null, null,
//				UserInfoColumns.LOGIN, "1");
//		if (pCursor.moveToFirst()) {
//			do {
//				UserInfo pUserInfo = this.buildUserInfoFromCursor(pCursor);
//				if (pUserInfo != null) {
//					pLogin.add(pUserInfo);
//				}
//			} while (pCursor.moveToNext());
//		}
//		if (!pCursor.isClosed()) {
//			pCursor.close();
//		}
//		return pLogin;
//	}
//
//	@Override
//	public List<UserInfo> getAll() {
//		List<UserInfo> pUserList = new ArrayList<UserInfo>();
//		Cursor pCursor = db.query(UserInfoTable.TABLE_NAME, new String[] {
//				UserInfoColumns.LOGIN, UserInfoColumns.PASS, UserInfoColumns.X,
//				UserInfoColumns.Y }, null, null, null, null, null, null);
//		if (pCursor.moveToFirst()) {
//			do {
//				UserInfo pUserInfo = this.buildUserInfoFromCursor(pCursor);
//				if (pUserInfo != null) {
//					pUserList.add(pUserInfo);
//				}
//			} while (pCursor.moveToNext());
//		}
//		if (!pCursor.isClosed()) {
//			pCursor.close();
//		}
//		return pUserList;
//	}
//
//	private UserInfo buildUserInfoFromCursor(Cursor pCursor) {
//		UserInfo pUserInfo = null;
//		if (pCursor != null) {
//			pUserInfo = new UserInfo();
//			pUserInfo.setId(pCursor.getLong(0));
//			pUserInfo.setLogin(pCursor.getString(1));
//			pUserInfo.setPass(pCursor.getString(2));
//			pUserInfo.setX(pCursor.getDouble(3));
//			pUserInfo.setY(pCursor.getDouble(4));
//		}
//		return pUserInfo;
//	}
//
//}
