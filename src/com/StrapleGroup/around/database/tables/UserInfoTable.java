package com.StrapleGroup.around.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class UserInfoTable {
	
	public static final String TABLE_NAME = "user_info";
	
	public static class UserInfoColumns implements BaseColumns{
		public static final String LOGIN = "login";
		//temporary,
		public static final String PASS = "pass";
		//end temporary;
		public static final String X = "x";
		public static final String Y = "y";
	}
	
	public static void onCreate(SQLiteDatabase db){
		StringBuilder build = new StringBuilder();
		build.append("CREATE TABLE " + UserInfoTable.TABLE_NAME + " (");
		build.append(UserInfoColumns.LOGIN + " TEXT, ");
		build.append(UserInfoColumns.PASS + " TEXT, ");
		build.append(UserInfoColumns.X + " DOUBLE, ");
		build.append(UserInfoColumns.Y + " DOUBLE");
		build.append(");");
		db.execSQL(build.toString());
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + UserInfoTable.TABLE_NAME);
		UserInfoTable.onCreate(db);
	}
}
