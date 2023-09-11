package com.example.trackerjava.repository;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import com.example.trackerjava.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import io.reactivex.Completable;
import io.reactivex.Single;

public class AuthAndRegRepository {


    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db;

    public WeakReference<Context> contextWeakReference;

    public AuthAndRegRepository(Context context) {
        contextWeakReference = new WeakReference<>(context);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }
    public AuthAndRegRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @SuppressLint("MissingPermission")
    public Single<Location> getUserCoordinates() {
        Context context = contextWeakReference.get();
        if (context == null) {
            return Single.error(new Exception("Context is not available"));
        }
        return Single.create(emitter -> {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            emitter.onSuccess(location);
                        } else {
                            emitter.onError(new Exception(context.getString(R.string.location_not_availableE)));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<FirebaseUser> getCurrentUser() {
        return Single.create(emitter -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                emitter.onSuccess(user);
            } else {
                emitter.onError(new Exception("User not logged in"));
            }
        });
    }

    public Completable registrationUser(String name, String email) {
        return Completable.create(emitter -> {
            if (firebaseAuth.getCurrentUser() != null) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                db.collection("users")
                        .document(userId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if ((document.exists())) {
                                    emitter.onError(new Exception("User data already exists in Firestore"));
                                } else {
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("name", name);
                                    userData.put("email", email);
                                    db.collection("users")
                                            .document(userId)
                                            .set(userData)
                                            .addOnSuccessListener(data -> emitter.onComplete())
                                            .addOnFailureListener(emitter::onError);
                                }
                            } else {
                                emitter.onError(new Exception("Firestore query failed"));
                            }
                        });
            } else {
                emitter.onError(new Exception("User not logged in"));
            }
        });
    }

   /* @SuppressLint("MissingPermission")
    public Single<Location> getUserCoordinates(Context context) {
        return Single.create(emitter -> {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            emitter.onSuccess(location);
                        } else {
                            emitter.onError(new Exception(context.getString(R.string.location_not_availableE)));
                        }
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }*/

    public Single<Boolean> sendLocationToFirestore(Location location) {
        return Single.create(emitter -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            String userId = currentUser.getUid();
            db.collection("users")
                    .document(userId)
                    .update("latitude", location.getLatitude(), "longitude", location.getLongitude())
                    .addOnSuccessListener(aVoid ->{
                       emitter.onSuccess(true);
                    })
                    .addOnFailureListener(error ->{
                        emitter.onError(error);
                    });
        });
    }

  /*  public void setContext(Context context) {
        this.context = context;
    }*/
}


