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
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import com.example.trackerjava.model.LocationData;
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
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyForegroundService extends Service {
    private final MyRoomDB myRoomDB;
    private static final String CHANNEL_ID = "my_channel_id";
    private FusedLocationProviderClient fusedLocationProviderClient;

    public MyForegroundService() {
        myRoomDB = MyRoomDB.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationUpdates();
    }

    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        return START_STICKY;
    }


    @SuppressLint("CheckResult")
      private void saveLocationData(Location location) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getCurrentUser().getUid();
        long timeCoordinate = System.currentTimeMillis();

        LocationData locationUser = new LocationData(location.getLatitude(), location.getLongitude(), timeCoordinate, 0);

        Completable.fromAction(() -> {
            long newCoordinateInRoom = myRoomDB.getLocationDao().insertLocation(locationUser);

             if (newCoordinateInRoom != -1) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                      long timeToServer = System.currentTimeMillis();

                    Map<String, Object> locationData = new HashMap<>();
                    locationData.put("userId", userId);
                    locationData.put("latitude", location.getLatitude());
                    locationData.put("longitude", location.getLongitude());
                    locationData.put("time", timeCoordinate);
                    locationData.put("timeToServer", timeToServer);

                    DocumentReference documentReference = db.collection("location").document(userId);
                    documentReference.set(locationData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Log.i("log", getString(R.string.Data_sent_to_cloud));
                                deleteDataFromRoom(locationUser);
                            })
                            .addOnFailureListener(error -> {
                                Log.i("log", getString(R.string.data_not_sent_to_cloud));
                            });
                }else {
                    Log.i("log", getString(R.string.current_user_is_missing));
                }
            }else {
                 Log.i("log", getString(R.string.db_is_empty));
             }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() ->{
            Log.i("log", getString(R.string.operations_performed_successfully));
        },throwable -> {
            Log.i("log", getString(R.string.failed));
        });

    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "LocationData Channel Name",
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

    private void stopForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE);
    }


    private Notification createNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,  CHANNEL_ID)
                .setContentTitle(getString(R.string.my_foreground_service))
                .setContentText(getString(R.string.tracking_your_location))
                .setSmallIcon(R.drawable.location_searching)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

   @SuppressLint("MissingPermission")
   private void setupLocationUpdates() {
       LocationRequest.Builder builder = new LocationRequest.Builder(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
               .setIntervalMillis(10 * 60 * 1000)
               .setMinUpdateDistanceMeters(60.0f);
       LocationCallback locationCallback = new LocationCallback() {
           @Override
           public void onLocationResult(LocationResult locationResult) {
               if (locationResult != null) {
                   Location location = locationResult.getLastLocation();
                   saveLocationData(location);
               }
           }
       };
       fusedLocationProviderClient.requestLocationUpdates(builder.build(), locationCallback, null);
   }

    @SuppressLint("CheckResult")
    private void deleteDataFromRoom(LocationData locationData){
        Completable.fromAction(()-> myRoomDB.getLocationDao().deleteLocationFromRoom(locationData))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()->{
                    Log.e("log", "Coordinates successfully removed from Room database");
                }, throwable -> {
                    Log.e("log", "Failed to delete data from Room");
                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground();
    }
}
