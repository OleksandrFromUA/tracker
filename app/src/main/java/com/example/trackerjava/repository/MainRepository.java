package com.example.trackerjava.repository;


import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.trackerjava.MyRoomDB;
import com.example.trackerjava.UserDao;
import com.google.firebase.auth.FirebaseAuth;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
    public LiveData<Boolean> getRegisteredUser(String uid) {
        Single.fromCallable(() -> myRoomDB.getDao().getUserById(uid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                            if (user != null) {
                                isLogged.postValue(true);
                            } else {
                                isLogged.postValue(false);
                            }
                        }, throwable -> {
                    Log.e("error", "Произошла ошибка: " + throwable.getMessage());
                        }

                );
        return isLogged;
    }

    public Completable deleteDataFromRoom(){
        return Completable.fromAction(() ->{
            firebaseAuth.signOut();
            myRoomDB.getDao().deleteAllUsers();
            myRoomDB.getLocationDao().deleteAllUsersByCoordination();
isLogged.postValue(false);
        }).subscribeOn(Schedulers.io());

    }
}

