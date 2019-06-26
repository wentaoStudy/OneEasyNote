package com.wentao.xrichtextdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.wentao.xrichtextdemo.MyApplication;
import com.wentao.xrichtextdemo.bean.Note;
import com.wentao.xrichtextdemo.bean.Photo;
import com.wentao.xrichtextdemo.util.CommonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PhotoDao {

    private MyOpenHelper helper;

    public PhotoDao(Context context) {
        helper = new MyOpenHelper(context);
    }

    /**
     * 查询所有笔记
     */
    public List<Photo> queryNotesAll() {
        SQLiteDatabase db = helper.getWritableDatabase();

        List<Photo> photoList = new ArrayList<>();
        Photo photo ;
        String sql ;
        Cursor cursor = null;
        try {
            sql = "select * from db_photo " ;
            cursor = db.rawQuery(sql, null);
            //cursor = db.query("note", null, null, null, null, null, "n_id desc");
            while (cursor.moveToNext()) {
                //循环获得展品信息
                photo = new Photo();
                photo.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
                photo.setId(cursor.getInt(cursor.getColumnIndex("id")));
                photoList.add(photo);
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
        return photoList;
    }

    /**
     * 插入笔记
     */
    public long insertNote(Photo photo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "insert into  db_photo(userId  , id  , photo_name )" +
                "values(?,?,?)";

        long ret = 0;
        //sql = "insert into ex_user(eu_login_name,eu_create_time,eu_update_time) values(?,?,?)";
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransaction();
        try {
            stat.bindString(1, photo.getUserId() );
            stat.bindLong(2, photo.getId());
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
     * 更新笔记
     * @param photo
     */
    public void updateNote(Photo photo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", photo.getUserId());
        values.put("id", photo.getId());
        db.update("db_photo", values, "id", new String[]{photo.getId()+""});
        db.close();
    }

    /**
     * 删除笔记
     * 暂时还没有添加网络端功能
     */
    public int deleteNote(Photo photo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int ret = 0;
        try {
            ret = db.delete("db_photo", "id=?", new String[]{ photo.getId()+ ""});
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
