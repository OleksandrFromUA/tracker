package com.example.trackerjava;

import android.app.Application;
import androidx.room.Room;

public class MyApplication extends Application {

    private static MyRoomDB myRoomDB;

    @Override
    public void onCreate() {
        super.onCreate();
        myRoomDB = Room.databaseBuilder(getApplicationContext(), MyRoomDB.class, "location_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static MyRoomDB getMyRoomDB() {
        return myRoomDB;
    }
}

