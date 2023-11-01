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
import io.reactivex.android.schedulers.AndroidSchedulers;
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
       return Completable.create(emitter -> {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io());
    }

    @SuppressLint("CheckResult")
    public Completable justSave(String email) {
        return Completable.fromAction(() ->  authAndRegRepository.saveToLocal(email)).subscribeOn(Schedulers.io());

    }

    @SuppressLint("CheckResult")
    public Completable registrationUserLocalDB(String email, String password) {
        return registration(email, password)
                .andThen(getCurrentUserUid())
                .flatMapCompletable(uid -> {
                    return authAndRegRepository.saveUserToRoom(uid, email)
                            .subscribeOn(Schedulers.io())
                            .onErrorResumeNext(throwable -> {
                                return Completable.error(new Exception("Error : " + throwable.getMessage()));
                            })
                            .andThen(Completable.complete());
                            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

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
            Log.i("app", "Success in method singInUser");
        }, throwable -> {
            Log.e("app", "Fail in method singInUser");
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

