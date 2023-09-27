package com.example.trackerjava.viewModel;

import android.annotation.SuppressLint;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;
import com.example.trackerjava.repository.AuthAndRegRepository;
import com.google.firebase.auth.FirebaseAuth;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AuthViewModel extends ViewModel {

    private final FirebaseAuth firebaseAuth;
    private final AuthAndRegRepository authAndRegRepository;
    public final ObservableField<String> email = new ObservableField<>("");
    public final ObservableField<String> password = new ObservableField<>("");
   // String emailValue = email.get();
  //  String passwordValue = password.get();

    public AuthViewModel(){
        firebaseAuth = FirebaseAuth.getInstance();
        authAndRegRepository = new AuthAndRegRepository();

    }

    @SuppressLint("CheckResult")
    public Completable registrationUserLocalDB(String email, String password) {
        return Completable.create(emitter -> {
           // firebaseAuth.createUserWithEmailAndPassword(emailValue, passwordValue)
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult ->{
                         authAndRegRepository.saveUserToRoom(email, password)
                                         .subscribeOn(Schedulers.io())
                                                 .observeOn(AndroidSchedulers.mainThread())
                                                         .subscribe(() -> {
                                                             emitter.onComplete();
                                                         }, throwable -> {
                                                             emitter.onError(new Exception(throwable));
                                                                 });
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable singInUser(String email, String password) {
        return Completable.create(emitter -> {
           // firebaseAuth.signInWithEmailAndPassword(emailValue, passwordValue)
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<Boolean> isUserExistsRoom(String uid){
        return authAndRegRepository.isUserExists(uid);
    }


}

