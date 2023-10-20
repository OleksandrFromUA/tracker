package com.example.trackerjava.repository;


import android.util.Log;
import androidx.lifecycle.LiveData;
import com.example.trackerjava.MyRoomDB;
import com.example.trackerjava.UserDao;
import com.example.trackerjava.model.User;
import com.google.firebase.auth.FirebaseAuth;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class MainRepository {
    private final MyRoomDB myRoomDB;
    private final UserDao userDao;
    private final FirebaseAuth firebaseAuth;

    public MainRepository() {
        myRoomDB = MyRoomDB.getInstance();
        userDao = myRoomDB.getDao();
        firebaseAuth = FirebaseAuth.getInstance();

    }
  public LiveData<User> getRegisteredUser(String uid) {
      Log.e("red", "зашли в метод getRegisteredUser в MainRepository");

      return myRoomDB.getDao().getUserById(uid);
    }

    public Completable deleteDataFromRoom(){
        return Completable.fromAction(() ->{
            firebaseAuth.signOut();
            myRoomDB.getDao().deleteAllUsers();
            myRoomDB.getLocationDao().deleteAllUsersByCoordination();
        }).subscribeOn(Schedulers.io());

    }

}