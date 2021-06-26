package com.anurag.covidhelp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.anurag.covidhelp.Helpers.FirebaseHelper;
import com.anurag.covidhelp.Helpers.OnCheckRegistered;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private TextInputEditText phoneNumber;
    private MaterialButton sendOtpBtn;
    private ProgressBar progressBar;

    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Disabling dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        phoneNumber = findViewById(R.id.phoneNumber);
        sendOtpBtn = findViewById(R.id.sendOtpBtn);

        progressBar = findViewById(R.id.progress);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!phoneValidation()) return;

                progressBar.setVisibility(View.VISIBLE);
                sendOtpBtn.setEnabled(false);

                new FirebaseHelper().isUserRegistered("+91"+phoneNumber.getText().toString(), new OnCheckRegistered() {
                    @Override
                    public void onCheckRegistered(boolean isRegistered) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendOtpBtn.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(Login.this, OTPActivity.class);
                                intent.putExtra("number", phoneNumber.getText().toString());
                                intent.putExtra("isRegistered", isRegistered);
                                startActivity(intent);
                            }
                        }, 500);
                    }
                });

//                Toast.makeText(Login.this, "User is " + (isRegistered ? "registered" : "not registered"), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser != null){
            Intent intent = new Intent(Login.this, Dashboard.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean phoneValidation() {
        String number = phoneNumber.getText().toString().trim();

        if (number.isEmpty()) {
            phoneNumber.setError("Phone number is required");
            phoneNumber.requestFocus();
            return false;
        }

        if (number.length() != 10) {
            phoneNumber.setError("Phone number must be 10 digits");
            phoneNumber.requestFocus();
            return false;
        }

        return true;
    }
}