package com.wentao.xrichtextdemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.wentao.xrichtextdemo.bean.Radio;

import java.util.ArrayList;
import java.util.List;

public class RadioDao {

    private MyOpenHelper helper;

    public RadioDao(Context context) {
        helper = MyOpenHelper.getInstance(context);
    }

    /**
     * 查询所有笔记
     */
    public List<Radio> queryRadiosAll() {
        SQLiteDatabase db = helper.getReadableDatabase();

        List<Radio> radioList = new ArrayList<>();
        Radio radio ;
        String sql ;
        Cursor cursor = null;
        try {
            sql = "select * from db_radio order by r_create_time desc";
            cursor = db.rawQuery(sql, null);
            //cursor = db.query("note", null, null, null, null, null, "n_id desc");
            while (cursor.moveToNext()) {
                //循环获得展品信息
                radio = new Radio();
                radio.setId(cursor.getString(cursor.getColumnIndex("r_id")));
                radio.setName(cursor.getString(cursor.getColumnIndex("r_name")));
                radio.setAddress(cursor.getString(cursor.getColumnIndex("r_address")));
                radio.setLength(cursor.getString(cursor.getColumnIndex("r_length")));
                radio.setCreateTime(cursor.getString(cursor.getColumnIndex("r_create_time")));

                radioList.add(radio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return radioList;
    }

    /**
     * 插入语音记录
     */
    public long insertRadio(Radio radio) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "insert into db_radio(r_id,r_name,r_create_time,r_length," +
                "r_address ) " +
                "values(?,?,?,?,?)";

        long ret = 0;
        //sql = "insert into ex_user(eu_login_name,eu_create_time,eu_update_time) values(?,?,?)";
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransaction();
        try {
            stat.bindString(1, radio.getId());
            stat.bindString(2, radio.getName());
            stat.bindString(3, radio.getCreateTime());
            stat.bindString(4, radio.getLength());
            stat.bindString(5, radio.getAddress());
            ret = stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }



    /**
     * 删除语音记录
     */
    public int deleteRadio(String radio_id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int ret = 0;
        try {
            ret = db.delete("db_radio", "r_id=?", new String[]{radio_id + ""});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return ret;
    }

}
