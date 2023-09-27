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
    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;
    @ColumnInfo(name = "coordinateTime")
    private long coordinateTime;

    public User() {
    }
    public User(String uid, String mail, double latitude, double longitude, long coordinateTime) {
        this.uid = uid;
        this.mail = mail;
        this.latitude = latitude;
        this.longitude = longitude;
        this.coordinateTime = coordinateTime;
    }

    public void setCoordinateTime(long coordinateTime) {
        this.coordinateTime = coordinateTime;
    }

    public long getCoordinateTime() {
        return coordinateTime;
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

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
