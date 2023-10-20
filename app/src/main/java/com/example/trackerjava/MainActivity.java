package com.example.trackerjava;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import android.os.Bundle;
import android.util.Log;
import com.example.trackerjava.databinding.ActivityMainBinding;
import com.example.trackerjava.viewModel.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser currentUser;
    private final MyRoomDB myRoomDB;
    private NavController navController;

    public MainActivity() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        myRoomDB = MyRoomDB.getInstance();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        MyWorker.startMyWorker(this);
        setupObservation();

    }

    private void setupObservation () {
           Log.e("red", "зашли в метод setupObservation() в Мейн Активити");
           if (currentUser != null) {
               String currentUidUser = currentUser.getUid();
               mainViewModel.isLogged(currentUidUser).observe(this, isLogged -> {
                   Log.e("red", "подписываемся на ЛайвДату в Мейн Активити");
                   if (isLogged != null) {
                       Log.e("red", "производим навигацию на trackerFragment");
                       navController.navigate(R.id.action_authFragment_to_trackerFragment);
                   } else {
                       Log.e("red", "находимся на Фрагменте аутентификации");
                       navController.navigate(R.id.action_trackerFragment_to_authFragment);
                   }
               });
           }
       }

    }
