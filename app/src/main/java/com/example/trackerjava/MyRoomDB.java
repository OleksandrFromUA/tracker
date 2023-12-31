package com.example.trackerjava;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.trackerjava.model.LocationData;
import com.example.trackerjava.model.User;

@Database(entities = {User.class, LocationData.class}, version = 2)
public abstract class MyRoomDB extends RoomDatabase {
    public abstract UserDao getDao();
    public abstract LocationDao getLocationDao();
    private static MyRoomDB instance;

    public static synchronized MyRoomDB getInstance(){
        if(instance == null){
            instance = Room.databaseBuilder(MyApplication.getAppContext(),
                            MyRoomDB.class, "location_database")
                    .build();
        }
        return instance;
    }
}
