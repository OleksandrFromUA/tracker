package com.example.trackerjava.repository;


import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
    private final MutableLiveData<Boolean> isLogged = new MutableLiveData<>();

    public MainRepository() {
        myRoomDB = MyRoomDB.getInstance();
        userDao = myRoomDB.getDao();
        firebaseAuth = FirebaseAuth.getInstance();
    }

  @SuppressLint("CheckResult")
  public LiveData<User> getRegisteredUser(String uid) {
      return myRoomDB.getDao().getUserById(uid);

  }
    public Completable deleteDataFromRoom(){
        return Completable.fromAction(() ->{
            firebaseAuth.signOut();
            myRoomDB.getDao().deleteAllUsers();
            myRoomDB.getLocationDao().deleteAllUsersByCoordination();
            isLogged.postValue(false);
            Log.e("error", "isLogged.postValue(false) in MainRepository deleteDataFromRoom()");
        }).subscribeOn(Schedulers.io());

    }
}

