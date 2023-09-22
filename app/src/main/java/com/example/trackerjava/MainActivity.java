package com.example.trackerjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import android.os.Bundle;
import com.example.trackerjava.databinding.ActivityMainBinding;
import com.example.trackerjava.viewModel.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser currentUser;
    private Toolbar toolbar;
    private final MyRoomDB myRoomDB;
    private final NavController navController;


    public MainActivity() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        //myRoomDB = MyRoomDB.getInstance(this.getApplication());
        myRoomDB = MyRoomDB.getInstance();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startMyWorker();
       // myRoomDB = MyApplication.getMyRoomDB();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        String currentUidUser = currentUser.getUid();

        mainViewModel.isLogged(currentUidUser).observe(this, isLogged -> {
            if (isLogged) {
                navController.navigate(R.id.action_authFragment_to_trackerFragment);
            } else {
                Utilit.showToast(this, R.string.login_error);
            }

        });

        TrackerFragment trackerFragment = (TrackerFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (trackerFragment != null) {
            toolbar = trackerFragment.getToolbar();
        }
        if (toolbar != null) {
            toolbar.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_logout) {

                    signOut();
                    myRoomDB.getDao().deleteAllUsers();
                    navigateToAuthFragment();
                    return true;
                }
                return false;
            });
        }
    }

    private void navigateToAuthFragment() {
        navController.navigate(R.id.action_trackerFragment_to_authFragment);
    }
    private void signOut() {
        firebaseAuth.signOut();
    }

    private void startMyWorker(){

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(
                MyWorker.class,
                15,
                TimeUnit.MINUTES
        )
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueue(syncWorkRequest);
    }
}

