package com.example.trackerjava.repository;




import com.example.trackerjava.MyRoomDB;
import com.example.trackerjava.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import io.reactivex.Completable;
import io.reactivex.Single;

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
            User user = new User(firebaseAuth.getCurrentUser().getUid(), email, 0, 0, 0);

            long userId = myRoomDB.getDao().insertUser(user);
            if (userId != -1) {
                emitter.onComplete();
            } else {
                emitter.onError(new Exception("Failed to insert user into Room database"));
            }
        });
    }

    public Single<Boolean> isUserExists(String uid) {
        return Single.fromCallable(() -> myRoomDB.getDao().getUserById(uid) != null);
    }


}


