package com.lzx.androidbeginner.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lizhenxin on 17-3-13.
 * 数据库定义类
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "Article";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "Article";
    private final String TYPE_TEXT = " TEXT,";

    public final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " ( "
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "title" + TYPE_TEXT
            + "url" + TYPE_TEXT
            + "html" + TYPE_TEXT
            + "deleted INTEGER "
            + ")";


    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
