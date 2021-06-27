package com.anurag.covidhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;


import java.util.Objects;

public class PatientDetails extends AppCompatActivity {

    private TextView name, bed, spo2, pulse, sBp, dBp, gFast, gRandom, date,last_date  ;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        Intent intent = getIntent();

        SharedPreferences userDetails = getSharedPreferences("userDetails", MODE_PRIVATE);
        String pin = userDetails.getString("pin", "");
        String hospital = userDetails.getString("hospital", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(pin).child(hospital);


        androidx.appcompat.widget.Toolbar myToolbar = findViewById(R.id.toolBarId);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.patientName);
        bed = findViewById(R.id.patientBed);
        spo2 = findViewById(R.id.spo2);
        pulse = findViewById(R.id.pulse);
        sBp = findViewById(R.id.sbp);
        dBp = findViewById(R.id.dbp);
        gFast = findViewById(R.id.gFasting);
        gRandom = findViewById(R.id.gRandom);
        date = findViewById(R.id.date);
        last_date = findViewById(R.id.lastDate);



//        temp = findViewById(R.id.gRandom);

      /*  String iPatientName = intent.getStringExtra("patientName");
        String iPatientBed = intent.getStringExtra("patientBed");
        int iSpo2 = intent.getIntExtra("spo2", 0);
        int iPulse = intent.getIntExtra("pulse", 0);
        int iSysBp = intent.getIntExtra("sysBp", 0);
        int iDiaBp = intent.getIntExtra("diaBp", 0);
        int iGlucoFast = intent.getIntExtra("glucoFast", 0);
        int iGlucoRandom = intent.getIntExtra("glucoRandom", 0);
//        String iTemperature = intent.getStringExtra("temperature");

        name.setText(iPatientName);
        bed.setText(iPatientBed);
        spo2.setText(String.valueOf(iSpo2));
        pulse.setText(String.valueOf(iPulse));
        sBp.setText(String.valueOf(iSysBp));
        dBp.setText(String.valueOf(iDiaBp));
        gFast.setText(String.valueOf(iGlucoFast));
        gRandom.setText(String.valueOf(iGlucoRandom));
        */


        String bed_id = intent.getStringExtra("patientBed");

        String d =String.valueOf(intent.getIntExtra("spo2",0));
        name.setText(intent.getStringExtra("patientName"));
        bed.setText(bed_id);
        spo2.setText(d);
        pulse.setText(String.valueOf(intent.getIntExtra("pulse",0)));
        sBp.setText(String.valueOf(intent.getIntExtra("sysBp",0)));
        dBp.setText(String.valueOf(intent.getIntExtra("diaBp",0)));
        gFast.setText(String.valueOf(intent.getIntExtra("glucoFast",0)));
        gRandom.setText(String.valueOf(intent.getIntExtra("glucoRandom",0)));


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {


                try {
                    for (DataSnapshot snap : snapshot.getChildren()){
                        if (snap.getKey().equals(bed_id)){
                            last_date.setText(snap.child("dates").child("last_updated").getValue().toString());
                            date.setText(snap.child("dates").child("added_on").getValue().toString());
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(PatientDetails.this, "Data Does Not Exist", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}