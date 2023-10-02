package com.example.trackerjava.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.trackerjava.repository.MainRepository;

public class MainViewModel extends ViewModel {
    private final MainRepository mainRepository;

    public MainViewModel() {

        mainRepository = new MainRepository();
    }

    public LiveData<Boolean> isLogged(String uid){

        return mainRepository.getRegisteredUser(uid);
    }
}
