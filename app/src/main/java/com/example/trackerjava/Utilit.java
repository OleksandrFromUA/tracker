package com.example.trackerjava;


import android.content.Context;
import android.widget.Toast;

public class Utilit {

    public static void showToast(Context context, int message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
