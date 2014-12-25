package com.StrapleGroup.around.database.tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Robert on 2014-12-25.
 */
public class LogsTable {

    public static final String TABLE_NAME = "logs_table";

    public static class LogsColumns implements BaseColumns {
        public static final String LOGIN_FRIEND = "login_friend";
        public static final String TYPE_LOG = "type_log";
        public static final String DATE = "date";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder build = new StringBuilder();
        build.append("CREATE TABLE " + FriendsInfoTable.TABLE_NAME + " (");
        build.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
        build.append(LogsColumns.LOGIN_FRIEND + " TEXT NOT NULL UNIQUE, ");
        build.append(LogsColumns.TYPE_LOG + " TEXT, ");
        build.append(LogsColumns.DATE + " LONG");
        build.append(");");
        db.execSQL(build.toString());
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FriendsInfoTable.TABLE_NAME);
        FriendsInfoTable.onCreate(db);
    }
}
