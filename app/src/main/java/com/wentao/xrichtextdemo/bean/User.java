package com.wentao.xrichtextdemo.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class User extends BmobObject {

    private String Number;

    private String Password;

    private String Name;

    private BmobFile Photo;

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public BmobFile getPhoto() {
        return Photo;
    }

    public void setPhoto(BmobFile photo) {
        Photo = photo;
    }
}
