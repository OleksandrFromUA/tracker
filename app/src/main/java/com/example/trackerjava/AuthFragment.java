package com.example.trackerjava;


import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.trackerjava.databinding.FragmentAuthBinding;
import com.example.trackerjava.viewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AuthFragment extends Fragment {
    private FragmentAuthBinding binding;
    private AuthViewModel authViewModel;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.setAuthViewModel(authViewModel);
        binding.setLifecycleOwner(this);
        firebaseAuth = FirebaseAuth.getInstance();

        binding.SIGNIN.setOnClickListener(v -> {
            String email = binding.InputNameText.getText().toString();
            String password = binding.InputPasswordText.getText().toString();
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                        authViewModel.singInUser(email, password)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    Utilit.showToast(requireContext(), R.string.user_in_the_system);
                                  },throwable -> {
                                    Utilit.showToast(requireContext(),R.string.this_user_is_not_in_the_system_please_register);
                                });
                    }else {
                        Utilit.showToast(requireContext(), R.string.email_and_password_fields_are_empty);
                    }

        });

        binding.signUp.setOnClickListener(v -> {
            String email = binding.InputNameText.getText().toString();
            String password = binding.InputPasswordText.getText().toString();
            currentUser = firebaseAuth.getCurrentUser();

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                if(currentUser != null) {
                    String currentUid = currentUser.getUid();
                    authViewModel.isUserExistsRoom(currentUid)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(userExist ->{
                                if(userExist){
                                    Utilit.showToast(requireContext(),R.string.user_already_registered_please_login);
                                }else {
                                    Utilit.showToast(requireContext(),R.string.user_not_registered_please_register);
                                    authViewModel.registrationUserLocalDB(currentUser.getUid(), email);
                                }
                            });
                }else {
                    Utilit.showToast(requireContext(),R.string.this_user_is_missing);
                }
            } else {
                Utilit.showToast(requireContext(),R.string.email_and_password_fields_are_empty);
            }

        });

        return binding.getRoot();
    }
}


