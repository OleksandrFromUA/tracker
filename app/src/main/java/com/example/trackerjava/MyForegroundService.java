package com.example.trackerjava;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.example.trackerjava.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MyForegroundService extends Service {
private final FirebaseAuth firebaseAuth;
private final FirebaseFirestore db;
private final MyRoomDB myRoomDB;

private static final String CHANNEL_ID = "my_channel_id";
    public MyForegroundService() {
        firebaseAuth = FirebaseAuth.getInstance();
        //myRoomDB = MyRoomDB.getInstance(this.getApplication());
        myRoomDB = MyRoomDB.getInstance();
        db = FirebaseFirestore.getInstance();
        }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        //myRoomDB = MyApplication.getMyRoomDB();

    }

    @SuppressLint("CheckResult")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        getUserCoordinates(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    String userEmail = firebaseAuth.getCurrentUser().getEmail();

                    User user = new User(userId, userEmail, location.getLatitude(), location.getLongitude());
                    long newUserInRoom = myRoomDB.getDao().insertUser(user);

                    if(newUserInRoom != -1){
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        if(currentUser != null){
                            Map<String, Object> locationData = new HashMap<>();
                            locationData.put("latitude", location.getLatitude());
                            locationData.put("longitude", location.getLongitude());

                            DocumentReference documentReference = db.collection("users").document(userId);
                             documentReference.set(locationData, SetOptions.merge())
                                    .addOnSuccessListener(aVoid ->{
                                        Utilit.showToast(this, R.string.Data_sent_to_cloud);
                                    })
                                    .addOnFailureListener(error ->{
                                        Utilit.showToast(this, R.string.data_not_sent_to_cloud);
                                    });
                        }

                    }
                }, error -> {
                    Utilit.showToast(this, R.string.coordinates_not_received);
                });

return START_STICKY;
    }





@SuppressLint("MissingPermission")
private Single<Location> getUserCoordinates(Context context){
        return Single.create(emitter -> {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
fusedLocationProviderClient.getLastLocation()
        .addOnSuccessListener(location -> {
            if(location != null){
                emitter.onSuccess(location);
            }else {
                emitter.onError(new Exception(context.getString(R.string.location_not_availableE)));
            }
        })
        .addOnFailureListener(emitter::onError);

        });
}

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "My Channel Name",
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
                .setContentText("Service is running...")
                .setSmallIcon(R.drawable.location_searching)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
