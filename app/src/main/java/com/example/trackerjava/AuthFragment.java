package com.example.trackerjava;


import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.trackerjava.databinding.FragmentAuthBinding;
import com.example.trackerjava.viewModel.AuthViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AuthFragment extends Fragment {
    private FragmentAuthBinding binding;
    private AuthViewModel authViewModel;

    private void requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

   @SuppressLint("CheckResult")
   private final ActivityResultLauncher<String> requestPermissionLauncher =
           registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
               if (isGranted) {
                   authViewModel.getUserLocation()
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(location -> {
                              if(location != null) {
                                  authViewModel.sendLocationUsersOnDB(location)
                                          .subscribeOn(Schedulers.io())
                                          .observeOn(AndroidSchedulers.mainThread())
                                          .subscribe(result -> {
                                              if (result) {
                                                  Utilit.showToast(requireContext(), R.string.successful_sending_to_firebase);
                                              } else {
                                                  Utilit.showToast(requireContext(), R.string.sending_error_occurred);

                                              }
                                          }, throwable -> {
                                              Utilit.showToast(requireContext(),R.string.error_occurred);
                                          });

                              }else{
                                  Utilit.showToast(requireContext(), R.string.location_not_available);
                              }
                           });



               } else {
                   Utilit.showToast(requireContext(), R.string.location_permission_denied);
               }
           });


   /* @Override
    public void onStart() {
        super.onStart();
        FirebaseUser cUser = mAuth.getCurrentUser();
        if(cUser != null){
            Toast.makeText(requireContext(), "User already exists", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(requireContext(), "Уou must register/sign In", Toast.LENGTH_SHORT).show();
        }
    }*/

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_auth, container, false);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
      //  authViewModel.setContext(this);
        binding.setAuthViewModel(authViewModel);
        binding.setLifecycleOwner(this);

        binding.SIGNIN.setOnClickListener(v -> {
            String email = binding.InputNameText.getText().toString();
            String password = binding.InputPasswordText.getText().toString();
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                        authViewModel.singInUser(email, password)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    requestLocationPermission();

                                    NavController navController = Navigation.findNavController(requireView());
                                    navController.navigate(R.id.action_authFragment_to_trackerFragment);
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

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                authViewModel.getCurrentUserFromDB()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMapCompletable(currentUser -> {
                            return FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(currentUser.getUid())
                                    .get()
                                    .continueWith(task -> {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot.exists()) {
                                                 Utilit.showToast(requireContext(),R.string.user_already_registered_please_login);
                                                //return Completable.complete();
                                            } else {
                                                return authViewModel.registrationUser(email, password)
                                                        .andThen(authViewModel.saveUser(currentUser.getUid(), email))
                                                        .doOnComplete(() -> Utilit.showToast(requireContext(),R.string.user_already_registered))
                                                        .doOnError(throwable -> {
                                                                    Utilit.showToast(requireContext(),R.string.user_not_registered_please_register);
                                                                });
                                            }
                                        } else {
                                            throw task.getException();
                                        }
                                    });
                        })
                        .subscribe(() -> {
                            Utilit.showToast(requireContext(),R.string.operation_succeeds);
                        }, throwable -> {
                            Utilit.showToast(requireContext(),R.string.error_occurred);
                        });
            } else {
                Utilit.showToast(requireContext(),R.string.email_and_password_fields_are_empty);
            }

        });

        return binding.getRoot();
    }
}