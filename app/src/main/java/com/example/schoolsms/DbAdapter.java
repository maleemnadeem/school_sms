package com.example.schoolsms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbAdapter {
    DbHelper dbHelper;

    public DbAdapter(Context context)
    {
        dbHelper = new DbHelper(context);
    }

    public long insertData(String url)
    {
        SQLiteDatabase dbb = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.URL, url);
        long id = dbb.insert(DbHelper.TABLE_NAME, null , contentValues);
        return id;
    }

    public String getData()
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columns = {dbHelper.UID,dbHelper.URL};
        Cursor cursor =db.query(dbHelper.TABLE_NAME,columns,null,null,null,null,null);
        String url = "";
        while (cursor.moveToNext())
        {
            int cid =cursor.getInt(cursor.getColumnIndex(dbHelper.UID));
            url =cursor.getString(cursor.getColumnIndex(dbHelper.URL));
        }
        return url;
    }
}
