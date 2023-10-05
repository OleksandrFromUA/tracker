package com.example.trackerjava.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.trackerjava.MyRoomDB;
import com.example.trackerjava.repository.MainRepository;
import com.google.firebase.auth.FirebaseAuth;

public class MainViewModel extends ViewModel {
    private final MainRepository mainRepository;
    private final MyRoomDB myRoomDB;
    private final FirebaseAuth firebaseAuth;


    public MainViewModel() {
    myRoomDB = MyRoomDB.getInstance();
    mainRepository = new MainRepository();
    firebaseAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Boolean> isLogged(String uid){

        return mainRepository.getRegisteredUser(uid);
    }

      public void deleteData(){
        firebaseAuth.signOut();
        myRoomDB.getDao().deleteAllUsers();
        myRoomDB.getLocationDao().deleteAllUsersByCoordination();
}

}
