package com.example.trackerjava.viewModel;



import android.annotation.SuppressLint;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.trackerjava.repository.MainRepository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private final MainRepository mainRepository;

    public MainViewModel() {

        mainRepository = new MainRepository();
    }

    public LiveData<Boolean> isLogged(String uid){

        return mainRepository.getRegisteredUser(uid);
    }

      @SuppressLint("CheckResult")
      public void deleteData(){
        mainRepository.deleteDataFromRoom()
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(() -> {
                      Log.e("tag", "Data successfully deleted");
                  }, throwable -> {
                      Log.e("tag", "Error occurred: " + throwable.getMessage());
                  });
}

}
