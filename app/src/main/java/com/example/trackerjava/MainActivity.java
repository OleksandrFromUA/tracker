package com.example.trackerjava;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.os.Bundle;
import android.os.PersistableBundle;
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
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();*/


        MyWorker.startMyWorker(this);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        if (currentUser != null) {
            String currentUidUser = currentUser.getUid();
            mainViewModel.isLogged(currentUidUser).observe(this, isLogged -> {
                if (isLogged) {
                    navController.navigate(R.id.action_authFragment_to_trackerFragment);
                } else {
                    navController.navigate(R.id.action_trackerFragment_to_authFragment);
                }

            });

        }

    }

}