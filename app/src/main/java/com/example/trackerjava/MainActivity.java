package com.example.trackerjava;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import android.os.Bundle;
import com.example.trackerjava.databinding.ActivityMainBinding;
import com.example.trackerjava.viewModel.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private FirebaseAuth firebaseAuth;
    private NavController navController;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupObservation();
        MyWorker.startMyWorker(this);
    }

    private void setupObservation(){
        if (currentUser != null) {
            mainViewModel.isLogged().observe(this, isLogged -> {
                if (isLogged != null ) {
                    navController.navigate(R.id.action_authFragment_to_trackerFragment);
                } else {
                    navController.navigate(R.id.action_trackerFragment_to_authFragment);
                }
            });
        }else {
            navController.navigate(R.id.action_trackerFragment_to_authFragment);
        }
    }
    }
