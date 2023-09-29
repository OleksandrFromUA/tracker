package com.example.trackerjava;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.trackerjava.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);


    }
    public static void startMyWorker(Context context){

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


       WorkManager.getInstance(context).enqueue(syncWorkRequest);
    }
    @NonNull
    @Override
    public Result doWork() {

        try {
            List<User> coordinatesFromRoom = getCoordinatesFromRoom();
            if (isInternetAvailable()) {
                boolean successSend = sendCoordinatesToFirebase(coordinatesFromRoom);
                if (successSend) {
                    deleteCoordinatesFromRoom(coordinatesFromRoom);
                    return Result.success();
                } else {

                    Utilit.showToast(this.getApplicationContext(), R.string.failed_to_send_data_to_the_cloud);
                    return Result.retry();
                }
            } else {
                Utilit.showToast(this.getApplicationContext(), R.string.no_internet);
                return Result.retry();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }


    private List<User> getCoordinatesFromRoom() {
        MyRoomDB myRoomDB = MyRoomDB.getInstance();
        UserDao userDao = myRoomDB.getDao();
        List<User> coordinates = userDao.getAllCoordinates();
        return coordinates;
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    private boolean sendCoordinatesToFirebase(List<User> coordinates) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String uidUser = currentUser.getUid();
            CollectionReference collectionReference = db.collection("users");
            DocumentReference documentReference = collectionReference.document(uidUser);
             //long timeSendCoordinate = System.currentTimeMillis();

            for (User coordinate : coordinates) {
                Map<String, Object> locationData = new HashMap<>();
                locationData.put("latitude", coordinate.getLatitude());
                locationData.put("longitude", coordinate.getLongitude());
                locationData.put("timeSendCoordinate", coordinate.getCoordinateTime());

                documentReference.set(locationData, SetOptions.merge());
            }
            return true;
        } else {
            return false;
        }
    }

    private void deleteCoordinatesFromRoom(List<User> coordinates) {

        MyRoomDB myRoomDB = MyRoomDB.getInstance();
        myRoomDB.getDao().deleteAllCoordinates();
        Utilit.showToast(this.getApplicationContext(), R.string.coordinates_successfully_removed_from_room_database);
        }

}
