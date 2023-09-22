package com.example.trackerjava;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.trackerjava.databinding.FragmentTrackerBinding;

public class TrackerFragment extends Fragment {

    private FragmentTrackerBinding binding;
    private LocationManager locationManager;
    private boolean isGpsEnabled = false;
    private boolean isTracking = false;
    private MyRoomDB myRoomDB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTrackerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

      //  myRoomDB = MyRoomDB.getInstance((Application) requireContext());
        myRoomDB = MyRoomDB.getInstance();
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        Toolbar toolbar = binding.toolbar;
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

          binding.Start.setOnClickListener(tracking -> {
            if (!isTracking) {
                startTracker();
            } else {
                stopTracker();
            }
        });

        return view;
    }

    private void startTracker(){
          requestLocationPermission();
          Intent serviceIntent = new Intent(requireContext(), MyForegroundService.class);
          requireContext().startService(serviceIntent);
          isTracking = true;
          binding.Start.setText(R.string.stop);
    }

    private void stopTracker(){
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


    private void requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @SuppressLint("CheckResult")
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (isGpsEnabled) {
                        Utilit.showToast(requireContext(), R.string.gps_on);
                    } else {
                        showGpsDialog();
                    }
                } else {
                    Utilit.showToast(requireContext(), R.string.permission_to_locate_is_denied);
                }
            });



    public Toolbar getToolbar() {
        return binding.toolbar;
    }


}
