package com.example.trackerjava.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "uid")
    private String uid;
    @ColumnInfo(name = "mail")
    private String mail;

    public User() {
    }
    public User(String uid, String mail) {
        this.uid = uid;
        this.mail = mail;

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public int getId() {

        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getMail() {
        return mail;
    }


}
