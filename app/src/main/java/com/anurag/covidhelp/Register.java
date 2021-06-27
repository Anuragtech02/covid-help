package com.anurag.covidhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.anurag.covidhelp.Helpers.BooleanValueChecker;
import com.anurag.covidhelp.Helpers.FirebaseHelper;
import com.anurag.covidhelp.Models.PinHospitalModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private TextInputEditText name, pin;
    private MaterialButton mSubmitBtn;
    private TextView addHospital;
    private ProgressBar pinLoading;

    private AutoCompleteTextView regTypeDropdown, hospitalDropdown;
    TextInputLayout hospitalMenu;
    private ArrayList<String> fetchedHospitals;

    private SharedPreferences regDetailsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Pattern PIN_CODE_PATTERN = Pattern.compile("^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$");

        name = findViewById(R.id.etName);
        addHospital = findViewById(R.id.newHospital);
        pin = findViewById(R.id.etPin);
        pinLoading = findViewById(R.id.pinLoading);

        mSubmitBtn = findViewById(R.id.btnSubmit);

        String[] REG_TYPES = new String[] {"Doctor", "Nurse"};

        ArrayAdapter<String> adapter =  new ArrayAdapter<>(getApplicationContext(),
                R.layout.dropdown_item_reg_type, REG_TYPES);

        regTypeDropdown = findViewById(R.id.menuView);
        regTypeDropdown.setAdapter(adapter);

        fetchedHospitals = new ArrayList<>();
        hospitalMenu = findViewById(R.id.hospitalMenu);
        hospitalDropdown = findViewById(R.id.hospitalMenuView);


        pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Toast.makeText(RegisterDoctor.this, "Length is: "+count, Toast.LENGTH_SHORT).show();
            }

            Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
            Runnable workRunnable;
            @Override public void afterTextChanged(Editable s) {
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> doSmth(s.toString());
                handler.postDelayed(workRunnable, 500 /*delay*/);
            }

            private final void doSmth(String str) {
                //
//                Toast.makeText(Register.this, "Yes this works", Toast.LENGTH_SHORT).show();
                if(PIN_CODE_PATTERN.matcher(str).matches()){
                    pinLoading.setVisibility(View.VISIBLE);
                    pin.setEnabled(false);
                    Toast.makeText(Register.this, "Value is: "+str, Toast.LENGTH_SHORT).show();

                    new FirebaseHelper().isValuePresent("pinCodes", "pinCode", str, new BooleanValueChecker() {
                        @Override
                        public void isValuePresent(boolean isPresent) {
                            if(isPresent){
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                                firestore.collection("pinCodes")
                                        .document(str)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    PinHospitalModel pinHospitalModel = task.getResult().toObject(PinHospitalModel.class);

                                                    ArrayAdapter<String> adapter =  new ArrayAdapter<>(getApplicationContext(),
                                                            R.layout.dropdown_item_reg_type,  pinHospitalModel.hospitals);

                                                    fetchedHospitals.addAll(pinHospitalModel.hospitals);
                                                    pin.setEnabled(true);
                                                    pinLoading.setVisibility(View.INVISIBLE);
                                                    hospitalMenu.setEnabled(true);
                                                    hospitalDropdown.setAdapter(adapter);
                                                }
                                            }
                                        });
                            }else{
                                hospitalMenu.setEnabled(false);
                                pin.setEnabled(true);
                                pinLoading.setVisibility(View.INVISIBLE);

                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Register.this);
                                builder.setTitle("Not found");
                                builder.setMessage("Oops! Couldn't find the provided pin code. Would you like to add new one to the list?");
                                builder.setPositiveButton("Create New", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addNewPin(pin.getText().toString());
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Do nothing
                                    }
                                });
                                builder.show();
                            }
                        }
                    });
                }
            }
        });

        addHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Register.this);
                builder.setTitle("Add new hospital");

                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.add_new_hospital, null);
                builder.setView(view);

                TextInputEditText hospital = view.findViewById(R.id.etNewHospital);

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(hospital.getText().toString().isEmpty()){
                            hospital.requestFocus();
                            hospital.setError("Hospital can't be empty");
                            return;
                        }

                        ArrayList<String> hospitals = new ArrayList<>(fetchedHospitals);
                        hospitals.add(hospital.getText().toString());


                        ArrayAdapter<String> newAdapter =  new ArrayAdapter<>(getApplicationContext(),
                                R.layout.dropdown_item_reg_type,  hospitals);

                        hospitalDropdown.setAdapter(newAdapter);
                        hospitalDropdown.setText(hospital.getText().toString(), false);

                        new FirebaseHelper().addHospital(pin.getText().toString(), hospitals, Register.this);
                    }
                });

                builder.setNegativeButton("Cancel", null);

                final AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                hospital.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.toString().isEmpty()){
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }else{
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                });

            }
        });


        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!detailsValidation()) return;

                String uid = FirebaseFirestore.getInstance().collection("users").document().getId();

                //Save data to db
                new FirebaseHelper().addUser(uid, name.getText().toString()
                        ,regTypeDropdown.getText().toString()
                        ,pin.getText().toString()
                        ,hospitalDropdown.getText().toString()
                        ,Register.this);

                //Save data locally
                regDetailsPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
                SharedPreferences.Editor editor = regDetailsPreferences.edit();
                editor.putString("name", name.getText().toString());
                editor.putString("designation", regTypeDropdown.getText().toString());
                editor.putString("pin", pin.getText().toString());
                editor.putString("hospital", hospitalDropdown.getText().toString());
                editor.apply();

                Toast.makeText(Register.this, name.getText().toString() , Toast.LENGTH_SHORT).show();

                sendUserTo(regTypeDropdown.getText().toString());
            }
        });
    }

    private void addNewPin(String prevPin){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Register.this);
        builder.setTitle("Add Pin code");

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.add_new_pin, null);
        builder.setView(view);

        TextInputEditText pin, hospital;
        pin = view.findViewById(R.id.etNewPin);
        hospital = view.findViewById(R.id.etNewHospital);

        pin.setText(prevPin);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Register.this, "Submitted", Toast.LENGTH_SHORT).show();

                String hpString = hospital.getText().toString();

                if(pin.getText().toString().isEmpty()){
                    pin.requestFocus();
                    pin.setError("PIN can't be empty");
                    return;
                }

                if(hpString.isEmpty()){
                    hospital.requestFocus();
                    hospital.setError("PIN can't be empty");
                    return;
                }

                Toast.makeText(Register.this, "value: "+ pin.getText().toString(), Toast.LENGTH_SHORT).show();

                ArrayList<String> hospitals = new ArrayList<>();
                hospitals.add(hpString);

                new FirebaseHelper().addPin(pin.getText().toString(), hospitals, Register.this);

                ArrayAdapter<String> adapter =  new ArrayAdapter<>(getApplicationContext(),
                        R.layout.dropdown_item_reg_type, hospitals);

                hospitalDropdown.setEnabled(true);
                hospitalDropdown.setAdapter(adapter);
                hospitalDropdown.setText(hpString);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        builder.show();
    }

    private boolean detailsValidation() {

        if (name.getText().toString().isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
            return false;
        }

        if(regTypeDropdown.getText().toString().isEmpty()){
            regTypeDropdown.requestFocus();
            regTypeDropdown.setError("Field is required");
            return false;
        }

        if(pin.getText().toString().isEmpty()){
            pin.setError("Pin is required");
            pin.requestFocus();
            return false;
        }

        if(hospitalDropdown.getText().toString().isEmpty()){
            hospitalDropdown.setError("Field is required");
            hospitalDropdown.requestFocus();
            return false;
        }

        return true;
    }

    private void sendUserTo(String dest){
        Intent intent = new Intent(Register.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}