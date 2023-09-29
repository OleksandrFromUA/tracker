package com.example.trackerjava;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.trackerjava.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;


public class MyForegroundService extends Service {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db;
    private final MyRoomDB myRoomDB;
    private static final String CHANNEL_ID = "my_channel_id";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;


    public MyForegroundService() {
        firebaseAuth = FirebaseAuth.getInstance();
        myRoomDB = MyRoomDB.getInstance();
        db = FirebaseFirestore.getInstance();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationRequest();
        setupLocationCallback();
        setupLocationUpdates();

    }


    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        return START_STICKY;
    }


    private void saveLocationData(Location location) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        String userEmail = firebaseAuth.getCurrentUser().getEmail();
        long timeCoordinate = System.currentTimeMillis();

        User user = new User(userId, userEmail, location.getLatitude(), location.getLongitude(), timeCoordinate);
        long newUserInRoom = myRoomDB.getDao().insertUser(user);

        if (newUserInRoom != -1) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                Map<String, Object> locationData = new HashMap<>();
                locationData.put("latitude", location.getLatitude());
                locationData.put("longitude", location.getLongitude());
                locationData.put("time", timeCoordinate);

                DocumentReference documentReference = db.collection("users").document(userId);
                documentReference.set(locationData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Utilit.showToast(this, R.string.Data_sent_to_cloud);
                        })
                        .addOnFailureListener(error -> {
                            Utilit.showToast(this, R.string.data_not_sent_to_cloud);
                        });
            }

        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
    }

    private void startForegroundService(){
        Notification notification = createNotification();
        startForeground(1, notification);
    }

    private Notification createNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,  CHANNEL_ID)
                .setContentTitle("My Foreground Service")
                .setContentText("Tracking your location...")
                .setSmallIcon(R.drawable.location_searching)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

    private void setupLocationRequest() {
        LocationRequest.Builder builder = new LocationRequest.Builder(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setIntervalMillis(10 * 60 * 1000)
                .setMinUpdateDistanceMeters(60.0f);
        locationRequest = builder.build();
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    saveLocationData(location);
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void setupLocationUpdates() {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
