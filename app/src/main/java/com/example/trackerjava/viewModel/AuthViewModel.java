package com.example.trackerjava.viewModel;

import android.annotation.SuppressLint;
import android.util.Log;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;
import com.example.trackerjava.repository.AuthAndRegRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AuthViewModel extends ViewModel {
    private final FirebaseAuth firebaseAuth;
    private final AuthAndRegRepository authAndRegRepository;
    public final ObservableField<String> email = new ObservableField<>("");
    public final ObservableField<String> password = new ObservableField<>("");
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public AuthViewModel() {
        firebaseAuth = FirebaseAuth.getInstance();
        authAndRegRepository = new AuthAndRegRepository();
    }

    public Completable registration(String email, String password) {
      //  Completable signInCompletable4 = Completable.create(emitter -> {
       return Completable.create(emitter -> {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io());
       /* Disposable disposable4 = compositeDisposable4.subscribe(()->{
        },throwable -> {
        });
        compositeDisposable4.add(disposable4);
        return ff;*/
    }

    @SuppressLint("CheckResult")
    public Completable justSave(String email) {
        Completable signInCompletable = Single.defer(() -> authAndRegRepository.isUserExists(firebaseAuth.getCurrentUser().getUid()))
                .flatMapCompletable(userExist -> {
                    if (userExist) {
                        Log.e("code", "User in the system(Room)");
                        return Completable.complete();
                    } else {
                        return Completable.defer(() -> {
                            try {
                                authAndRegRepository.saveToLocal(email);
                                Log.e("code", "User in the system(Room) after saveToLocal()");
                                return Completable.complete();
                            } catch (Exception e) {
                                return Completable.error(e);
                            }
                        });
                    }
                })
                .observeOn(Schedulers.io());
        //  .subscribeOn(AndroidSchedulers.mainThread())
        Disposable disposable = signInCompletable.subscribe(() -> {
            Log.e("app", "Succes");
        }, throwable -> {
            Log.e("app", "Fail");
        });

        compositeDisposable.add(disposable);
        return signInCompletable;
    }

    /*@SuppressLint("CheckResult")
    public Completable registrationUserLocalDB(String email, String password) {
        Completable signInCompletable2 = registration(email, password)
                .andThen(getCurrentUserUid())
                .flatMapCompletable(uid -> {
                    Log.e("app", "вызываем метод isUserExists в AuthViewModel");
                    return authAndRegRepository.isUserExists(uid)
                            .flatMapCompletable(userExist -> {
                                if (userExist) {
                                    Log.e("app", "находимся в методе registrationUserLocalDB в AuthViewModel");

                                    return Completable.complete();

                                } else {
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
                .subscribeOn(Schedulers.io());
        Disposable disposable2 = signInCompletable2.subscribe(() -> {
            Log.e("app", "Succes2");
        }, throwable -> {
            Log.e("app", "Fail2");
        });

        compositeDisposable.add(disposable2);
        return signInCompletable2;

    }*/

    @SuppressLint("CheckResult")
    public Completable registrationUserLocalDB(String email, String password) {
        return registration(email, password)
                .andThen(getCurrentUserUid())
                .flatMapCompletable(uid -> {
                    Log.e("app", "вызываем метод isUserExists в AuthViewModel");
                    return authAndRegRepository.isUserExists(uid)
                            .flatMapCompletable(userExist -> {
                                if (userExist) {
                                    Log.e("app", "находимся в методе registrationUserLocalDB в AuthViewModel");
                                    return Completable.complete();

                                } else {
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
                .subscribeOn(Schedulers.io());

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
        Completable signInCompletable3 = Completable.create(emitter -> {

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);

        }).subscribeOn(Schedulers.io());

        Disposable disposable3 = signInCompletable3.subscribe(() -> {
            Log.e("app", "Succes3");
        }, throwable -> {
            Log.e("app", "Fail3");
        });

        compositeDisposable.add(disposable3);
        return signInCompletable3;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}

