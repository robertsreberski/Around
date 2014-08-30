package com.StrapleGroup.around.database.tables;


import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class FriendsInfoTable {
    public static final String TABLE_NAME = "friends_info";

    public static class FriendsInfoColumns implements BaseColumns {
        public static final String LOGIN_FRIEND = "login_friend";
        public static final String X_FRIEND = "x_friend";
        public static final String Y_FRIEND = "y_friend";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder build = new StringBuilder();
        build.append("CREATE TABLE " + FriendsInfoTable.TABLE_NAME + " (");
        build.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");
        build.append(FriendsInfoColumns.LOGIN_FRIEND + " TEXT NOT NULL UNIQUE, ");
        build.append(FriendsInfoColumns.X_FRIEND + " DOUBLE, ");
        build.append(FriendsInfoColumns.Y_FRIEND + " DOUBLE");
        build.append(");");
        db.execSQL(build.toString());
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FriendsInfoTable.TABLE_NAME);
        FriendsInfoTable.onCreate(db);
    }
}
