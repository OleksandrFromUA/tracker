package com.example.trackerjava.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "location")
public class LocationData {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "coordinateTime")
    private long coordinateTime;

    @ColumnInfo(name = "timeToServer")
    private long timeToServer;

public LocationData(){

}
    public LocationData(double latitude, double longitude, long coordinateTime, long timeToServer) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.coordinateTime = coordinateTime;
        this.timeToServer = timeToServer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getCoordinateTime() {
        return coordinateTime;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCoordinateTime(long coordinateTime) {
        this.coordinateTime = coordinateTime;
    }

    public long getTimeToServer() {
        return timeToServer;
    }

    public void setTimeToServer(long timeToServer) {
        this.timeToServer = timeToServer;
    }
}
