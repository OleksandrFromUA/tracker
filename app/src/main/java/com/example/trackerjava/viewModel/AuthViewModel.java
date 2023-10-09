package com.example.trackerjava.viewModel;

import android.annotation.SuppressLint;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;
import com.example.trackerjava.repository.AuthAndRegRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


    public Completable registration(String email, String password) {
        return Completable.create(emitter -> {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        }).observeOn(Schedulers.io());
    }

    @SuppressLint("CheckResult")
    public Completable registrationUserLocalDB(String email, String password) {
        return registration(email, password)
                .andThen(getCurrentUserUid())
                .flatMapCompletable(uid ->{
                    return authAndRegRepository.isUserExists(uid)
                            .flatMapCompletable(userExist ->{
                                if ((userExist)){
                                    return Completable.complete();
                                }else {
                                   return authAndRegRepository.saveUserToRoom(uid, email)
                                           .onErrorResumeNext(throwable -> {
                                               return Completable.error(new Exception("Failed to save user to Room database"));
                                           });
                                }
                            })
                            .onErrorResumeNext(throwable -> {
                                return Completable.error(new Exception("Error checking user in Room database"));
                            });
                })
                .onErrorResumeNext(throwable -> {
                    return Completable.error(new Exception("Error during registration process"));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }
    private Single<String> getCurrentUserUid() {
        return Single.create(emitter -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                emitter.onSuccess(currentUser.getUid());
            } else {
                emitter.onError(new Exception("Current user is null"));
            }
        });
    }
            public Completable singInUser(String email, String password) {
        return Completable.create(emitter -> {
           // firebaseAuth.signInWithEmailAndPassword(emailValue, passwordValue)
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);

        }).observeOn(Schedulers.io());
    }

    /*public Single<Boolean> isUserExistsRoom(String uid){
        return authAndRegRepository.isUserExists(uid);
    }*/


}

