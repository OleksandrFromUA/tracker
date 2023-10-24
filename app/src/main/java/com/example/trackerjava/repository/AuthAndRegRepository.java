package com.example.trackerjava.repository;


import com.example.trackerjava.MyRoomDB;
import com.example.trackerjava.model.LocationData;
import com.example.trackerjava.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public class AuthAndRegRepository {
    private MyRoomDB myRoomDB;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db;

    public AuthAndRegRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        myRoomDB = MyRoomDB.getInstance();
    }

    public Completable saveUserToRoom(String name, String email) {
        return Completable.create(emitter -> {
            User user = new User(firebaseAuth.getCurrentUser().getUid(), email);
            LocationData locationData = new LocationData(0,0,0, 0);

            long userId = myRoomDB.getDao().insertUser(user);
            long locationU = myRoomDB.getLocationDao().insertLocation(locationData);
            if (userId != -1 && locationU != -1) {
                emitter.onComplete();
            } else {
                emitter.onError(new Exception("Failed to insert user into Room database"));

            }
        });
    }

    public void saveToLocal(String email) {
        Completable.fromAction(() -> {
            User user = new User(firebaseAuth.getCurrentUser().getUid(), email);
            LocationData locationData = new LocationData(0, 0, 0, 0);
            myRoomDB.getDao().insertUser(user);
            myRoomDB.getLocationDao().insertLocation(locationData);
        }).subscribeOn(Schedulers.io()).subscribe();
    }


  /*public Single<Boolean> isUserExists(String uid) {
      return Single.create(emitter -> {
          LiveData<User> userLiveData = myRoomDB.getDao().getUserById(uid);
          User user = userLiveData.getValue();
          if(user != null){
           emitter.onSuccess(true);
        }else {
            emitter.onError(new Exception("Пользователя нет в системе"));
        }

      });
  }*/

    }

