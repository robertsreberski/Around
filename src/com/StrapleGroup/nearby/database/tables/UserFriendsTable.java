package com.StrapleGroup.nearby.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class UserFriendsTable {
	public static final String TABLE_NAME = "user_friends";
	
	public static class UserFriendsColumns{
		public static final String USER_ID = "user_id";
		public static final String FRIENDS_ID = "friends_id";
	}
	
	public static void onCreate(SQLiteDatabase db){
		StringBuilder builder = new StringBuilder();
		builder.append("CREATE TABLE " + UserFriendsTable.TABLE_NAME + " (");
		builder.append(UserFriendsColumns.USER_ID + " INTEGER NOT NULL, ");
		builder.append(UserFriendsColumns.FRIENDS_ID + " INTEGER NOT NULL, ");
		builder.append("FOREIGN KEY(" + UserFriendsColumns.USER_ID + ") REFERENCES " + UserInfoTable.TABLE_NAME + "(" + BaseColumns._ID + "), ");
		builder.append("FOREIGN KEY(" + UserFriendsColumns.FRIENDS_ID + ") REFERENCES " + FriendsInfoTable.TABLE_NAME + "(" + BaseColumns._ID + "), ");
		builder.append("PRIMARY KEY ( " + UserFriendsColumns.USER_ID + " , " + UserFriendsColumns.FRIENDS_ID + ")");
		builder.append(");");
		db.execSQL(builder.toString());
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + UserFriendsTable.TABLE_NAME);
		UserFriendsTable.onCreate(db);
	}
}
