package com.wentao.xrichtextdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "radio.db";// 数据库文件名
    private final static int DB_VERSION = 1;// 数据库版本

    public MyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建分类表

        db.execSQL("create table db_radio(r_id String primary key , " +
                "r_name varchar, r_create_time varchar, r_length varchar , r_address varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
