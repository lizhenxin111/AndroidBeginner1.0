package com.lzx.androidbeginner.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzx.androidbeginner.db.DBHelper;
import com.lzx.androidbeginner.utils.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizhenxin on 17-3-24.
 * 数据库管理类
 */

public class DBManager {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public DBManager(Context context){
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insertArticle(String title, String url, String html){
        long result = 0;
        int num = db.query(DBHelper.TABLE_NAME, null, "title = ?", new String[]{title}, null, null, null).getCount();
        if (num == 0){
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("html", html);
            values.put("url", url);
            values.put("deleted", 0);
            result = db.insert(DBHelper.TABLE_NAME, null, values);
        }
        return result;
    }

    public long deleteArticle(String title){
        return db.delete(DBHelper.TABLE_NAME, "title = ?", new String[]{title});
    }

    public List<Article> getArticles(){
        List<Article> list = new ArrayList<>();

        Cursor cursor = db.query(DBHelper.TABLE_NAME, null, "deleted = ?", new String[]{String.valueOf(0)}, null, null, null);
        if (cursor.getCount() != 0){
            cursor.moveToFirst();
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String html = cursor.getString(cursor.getColumnIndex("html"));

                Article article = new Article(title, url, html);
                list.add(article);
            }while (cursor.moveToNext());
        }
        return list;
    }
}
