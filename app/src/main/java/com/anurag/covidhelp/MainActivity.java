package com.anurag.covidhelp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// This activity serves as
// SplashScreen for the App
public class MainActivity extends AppCompatActivity {

    private FirebaseUser mCurrentUser;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Disabling dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (mCurrentUser != null) {
                    intent = new Intent(MainActivity.this, Dashboard.class);
                } else {
                    intent = new Intent(MainActivity.this, Login.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}