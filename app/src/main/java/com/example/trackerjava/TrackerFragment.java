package com.example.trackerjava;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.trackerjava.databinding.FragmentTrackerBinding;
import com.example.trackerjava.viewModel.MainViewModel;
import java.util.HashMap;
import java.util.Map;

public class TrackerFragment extends Fragment {
    private FragmentTrackerBinding binding;
    private LocationManager locationManager;
    private boolean isGpsEnabled = false;
    private boolean isTracking = false;

    private MainViewModel mainViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrackerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        Toolbar toolbar = binding.toolbar;
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        binding.Start.setOnClickListener(tracking -> {
            if (!isTracking) {
                // startTracker();
                requestLocationAndNotificationPermission();
            } else {
                stopTracker();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            mainViewModel.deleteData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    private void startTracker() {
        //  requestLocationPermission();
        Intent serviceIntent = new Intent(requireContext(), MyForegroundService.class);
        requireContext().startForegroundService(serviceIntent);
        isTracking = true;
        binding.Start.setText(R.string.stop);
    }

    @SuppressLint("NewApi")
    private void stopTracker() {
        Intent serviceIntent = new Intent(requireContext(), MyForegroundService.class);
        requireContext().stopService(serviceIntent);
        isTracking = false;
        binding.Start.setText(R.string.start);
    }

    private void showGpsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(R.string.activate_gps_in_the_settings_to_use_this_feature)
                .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

   /* @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestLocationPermission() {
         requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }*/

   /* @SuppressLint({"CheckResult", "NewApi"})
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (isGpsEnabled) {
                        requestNotificationPermission();
                        Utilit.showToast(requireContext(), R.string.gps_on);
                    } else {
                        showGpsDialog();
                    }
                } else {
                    Utilit.showToast(requireContext(), R.string.permission_to_locate_is_denied);
                }
            });*/

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestNotificationPermission() {
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (notificationManager.areNotificationsEnabled()) {
                Utilit.showToast(requireContext(), R.string.Настройки_уведомлений_доступны);
            } else {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Utilit.showToast(requireContext(), R.string.Настройки_уведомлений_недоступны);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Utilit.showToast(requireContext(), R.string.Служба_уведомлений_недоступна);
        }
    }


    private void requestLocationAndNotificationPermission() {
        @SuppressLint("InlinedApi") String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
        };
        requestPermissionLauncher.launch(permissions);
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissionResult -> {
                Map<String, Boolean> isGranted = new HashMap<>();
                if (permissionResult != null) {
                    for (String permission : permissionResult.keySet().toArray(new String[0])) {
                        isGranted.put(permission, ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED);
                    }
                }
                    if (isGranted.containsKey(Manifest.permission.ACCESS_FINE_LOCATION) &&
                        isGranted.containsKey(Manifest.permission.POST_NOTIFICATIONS) &&
                        Boolean.TRUE.equals(isGranted.get(Manifest.permission.ACCESS_FINE_LOCATION)) &&
                        Boolean.TRUE.equals(isGranted.get(Manifest.permission.POST_NOTIFICATIONS))) {

                    if (checkGpsAndStartService()) {
                   // if (checkGpsEnabled()) {
                        startTracker();
                      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            requestNotificationPermission();
                        }*/
                    } else {
                        Utilit.showToast(requireContext(), R.string.gps_is_off);
                        showGpsDialog();
                    }
                    Utilit.showToast(requireContext(), R.string.the_user_did_not_grant_both_permissions);
                }
            });

    /*private boolean checkGpsEnabled() {
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGpsEnabled;
    }*/

     private boolean checkGpsAndStartService() {
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnabled ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestNotificationPermission();
            }
            Utilit.showToast(requireContext(), R.string.gps_on);
            return true;
        } else {
            showGpsDialog();
        }
        return false;
    }
}