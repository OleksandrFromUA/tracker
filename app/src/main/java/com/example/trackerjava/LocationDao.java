package com.example.trackerjava;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.trackerjava.model.LocationData;
import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    long insertLocation(LocationData location);
    @Query("DELETE FROM location")
     void deleteAllUsersByCoordination();
    @Query("SELECT * FROM location")
    List<LocationData> getAllCoordinates();
    @Delete
    void deleteLocationFromRoom(LocationData locationData);
}
