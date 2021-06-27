package com.anurag.covidhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anurag.covidhelp.Helpers.FirebaseHelper;
import com.anurag.covidhelp.Helpers.OnUserFetched;
import com.anurag.covidhelp.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private static final String KEY_VERIFICATION_ID = "key_verification_id";
    private String contact;


    private TextInputEditText etOtp;
    private MaterialButton submitBtn;

    private ProgressBar loading;

    // Fetched from previous activity intent
    private boolean isUserRegistered = false;
    private SharedPreferences userDetailsPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        mAuth = FirebaseAuth.getInstance();

        etOtp = findViewById(R.id.etOtp);
        submitBtn = findViewById(R.id.submitBtn);
        loading = findViewById(R.id.progress);

        Intent intent = getIntent();

        String number = intent.getStringExtra("number");
        contact = number;
        boolean isRegistered = intent.getBooleanExtra("isRegistered", false);

        isUserRegistered = isRegistered;

        if(number != null && !number.isEmpty()){
            sendVerificationCode(number);

            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loading.setVisibility(View.VISIBLE);
                    submitBtn.setEnabled(false);
                    String otp = etOtp.getText().toString();
                    if(otp == null || otp.isEmpty()){
                        etOtp.setError("OTP can't be empty :(");
                        submitBtn.setEnabled(true);
                        return;
                    }

                    // Restore instance state
                    // put this code after starting phone number verification
                    if (mVerificationId == null && savedInstanceState != null) {
                        onRestoreInstanceState(savedInstanceState);
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            verifyVerificationCode(otp);
                        }
                    }, 500);

                }
            });
        }
    }

    // These methods prevent app from losing verificationID
    // in case of low memory or slow internet connnection.
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_VERIFICATION_ID,mVerificationId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationId = savedInstanceState.getString(KEY_VERIFICATION_ID);
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);


    }

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            Toast.makeText(OTPActivity.this, "Reached here", Toast.LENGTH_SHORT).show();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                loading.setVisibility(View.VISIBLE);
                etOtp.setText(code);
                etOtp.setEnabled(false);
                Toast.makeText(OTPActivity.this, "Reached here", Toast.LENGTH_SHORT).show();
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            submitBtn.setEnabled(true);
            Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            mResendToken = forceResendingToken;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTPActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful so start the next desired activity

                            // If users is already registered, redirect to homepage/dashboard, else redirect
                            // to registration page
                            Intent intent;
                            if(isUserRegistered){
                                intent = new Intent(OTPActivity.this, Dashboard.class);
                                userDetailsPreference = getSharedPreferences("userDetails", MODE_PRIVATE);

                                new FirebaseHelper().getUser(contact, new OnUserFetched() {
                                    @Override
                                    public void OnUserFetched(UserModel user) {

                                        Toast.makeText(OTPActivity.this, user.name , Toast.LENGTH_SHORT).show();

                                        //Save user data locally
                                        SharedPreferences.Editor editor = userDetailsPreference.edit();
                                        editor.putString("name", user.name);
                                        editor.putString("contact", contact);
                                        editor.putString("designation", user.designation);
                                        editor.putString("pin", user.pinCode);
                                        editor.putString("hospital", user.hospital);
                                        editor.apply();

                                    }
                                });
//                                String des = userDetailsPreference.getString("designation", "nurse");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            }else{
                                intent = new Intent(OTPActivity.this, Register.class);
                            }
                            loading.setVisibility(View.GONE);
                            startActivity(intent);

                        } else {
                            submitBtn.setEnabled(true);

                            //verification unsuccessful.. display an error message
                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            loading.setVisibility(View.GONE);

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }
}