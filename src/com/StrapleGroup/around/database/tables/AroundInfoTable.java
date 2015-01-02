package com.StrapleGroup.around.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Robert on 2014-12-29.
 */
public class AroundInfoTable {

    public static final String TABLE_NAME = "around_table";

    public static class AroundColumns implements BaseColumns {
        public static final String LOGIN_FRIEND = "login_friend";
        public static final String DISTANCE = "distance";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder build = new StringBuilder();
        build.append("CREATE TABLE " + AroundInfoTable.TABLE_NAME + " (");
        build.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
        build.append(AroundColumns.LOGIN_FRIEND + " TEXT NOT NULL UNIQUE, ");
        build.append(AroundColumns.DISTANCE + " FLOAT");
        build.append(");");
        db.execSQL(build.toString());
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AroundInfoTable.TABLE_NAME);
        AroundInfoTable.onCreate(db);
    }
}
