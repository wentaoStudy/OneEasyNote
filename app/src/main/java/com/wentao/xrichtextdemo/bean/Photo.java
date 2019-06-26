package com.wentao.xrichtextdemo.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Photo extends BmobObject implements Serializable {
    private String userId;
    private int id;//笔记ID
    public BmobFile Photo;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BmobFile getPhoto() {
        return Photo;
    }

    public void setPhoto(BmobFile photo) {
        Photo = photo;
    }
}
