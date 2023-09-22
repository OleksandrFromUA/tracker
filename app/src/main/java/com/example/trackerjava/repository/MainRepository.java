package com.example.trackerjava.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.trackerjava.MyRoomDB;
import com.example.trackerjava.UserDao;
import com.example.trackerjava.model.User;

public class MainRepository {
    private final MyRoomDB myRoomDB;
    private final UserDao userDao;
    private final MutableLiveData<Boolean> isLogged = new MutableLiveData<>();
    private final Context context;

    public MainRepository(Context context) {
        this.context = context;
       // myRoomDB = MyRoomDB.getInstance((Application)context);
        myRoomDB = MyRoomDB.getInstance();
        userDao = myRoomDB.getDao();
    }

    public LiveData<Boolean> getRegisteredUser(String uid){
        User user = userDao.getUserById(uid);
        isLogged.postValue(user != null);
        return isLogged;
    }

}
