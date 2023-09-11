package com.example.trackerjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.trackerjava.viewModel.AuthViewModel;

public class MainActivity extends AppCompatActivity {
    private final AuthViewModel authViewModel;

    public MainActivity(AuthViewModel authViewModel) {
        this.authViewModel = authViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        if (authViewModel.isUserLoggedIn()) {

        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity, new AuthFragment())
                    .commit();
        }*/

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        authViewModel.signOut();
    }
}