package com.example.trackerjava;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.example.trackerjava.model.User;
import java.util.List;


@Dao
public interface UserDao {
    @Insert
    long insertUser(User user);
    @Transaction
    @Query("SELECT * FROM users WHERE uid = :uid")
    User getUserById(String uid);

    @Query("DELETE FROM users")
    void deleteAllUsers();

    @Query("SELECT * FROM users")
    List<User> getAllCoordinates();

    @Query("DELETE FROM users")
    void deleteAllCoordinates();

}

