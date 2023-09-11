package com.example.trackerjava.viewModel;

import android.location.Location;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;
import com.example.trackerjava.repository.AuthAndRegRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.grpc.Context;
import io.reactivex.Completable;
import io.reactivex.Single;


public class AuthViewModel extends ViewModel {

    private final Context context;
    private final FirebaseAuth firebaseAuth;
    private final AuthAndRegRepository authAndRegRepository;
    public final ObservableField<String> email = new ObservableField<>("");
    public final ObservableField<String> password = new ObservableField<>("");

    public AuthViewModel(Context context) {
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        authAndRegRepository = new AuthAndRegRepository();

    }

    public Completable registrationUser(String email, String password) {
        return Completable.create(emitter -> {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable singInUser(String email, String password) {
        return Completable.create(emitter -> {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public boolean isUserLoggedIn() {

        return firebaseAuth.getCurrentUser() != null;
    }


    public void signOut() {
        firebaseAuth.signOut();
    }

    public Single <FirebaseUser> getCurrentUserFromDB(){
        return authAndRegRepository.getCurrentUser();
    }

    public Completable saveUser(String name, String email){
        return authAndRegRepository.registrationUser(name, email);
    }


    public Single<Boolean> sendLocationUsersOnDB(Location location){
        return authAndRegRepository.sendLocationToFirestore(location);
    }

    public Single<Location> getUserLocation(){
       return authAndRegRepository.getUserCoordinates();
    }

   /* public void setContext(Context context) {
        this.context = context;
    }*/
}

