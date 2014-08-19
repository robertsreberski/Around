package com.StrapleGroup.around.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.StrapleGroup.around.database.tables.FriendsInfoTable;

public class OpenHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "DataTables.db";
	private Context context = null;
	
	public OpenHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		UserInfoTable.onCreate(db);
		
		FriendsInfoTable.onCreate(db);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//		UserInfoTable.onUpgrade(db, oldVersion, newVersion);
		
		FriendsInfoTable.onUpgrade(db, oldVersion, newVersion);
		
	}

}
