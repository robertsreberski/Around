package com.StrapleGroup.around.database.daos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.StrapleGroup.around.database.base.LogInfo;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;
import com.StrapleGroup.around.database.tables.LogsTable;

/**
 * Created by Robert on 2014-12-25.
 */
public class LogsDao {
    private static final String INSERT_ALL = "insert into "
            + LogsTable.TABLE_NAME + "("
            + LogsTable.LogsColumns.LOGIN_FRIEND + " , "
            + LogsTable.LogsColumns.TYPE_LOG + " , "
            + LogsTable.LogsColumns.DATE
            + ") values (?, ?, ?)";
    private SQLiteDatabase db;
    private SQLiteStatement insertStatement;

    public LogsDao(SQLiteDatabase db) {
        this.db = db;
        insertStatement = db.compileStatement(INSERT_ALL);
    }

    public void save(LogInfo logInfo) {
        insertStatement.clearBindings();
        insertStatement.bindString(1, logInfo.getLogin());
        insertStatement.bindString(2, logInfo.getType());
        insertStatement.bindLong(3, logInfo.getTime());
        insertStatement.executeInsert();
    }

}
