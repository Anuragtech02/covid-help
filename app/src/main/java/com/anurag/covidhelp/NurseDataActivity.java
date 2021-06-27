package com.anurag.covidhelp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NurseDataActivity extends AppCompatActivity {

    private TextView patientName;

    private TextInputEditText etSpo2, etBodyTemp, etPulse;
    private TextInputEditText etSysBp, etDiaBp, etGFasting, etGRandom;
    private MaterialButton addPatient, submit, add_bed;
    private AutoCompleteTextView bedMenu;

    String bed_choosed ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_bed);

        androidx.appcompat.widget.Toolbar myToolbar = findViewById(R.id.toolBarId);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Input Fields
        patientName = findViewById(R.id.patientName);
        etSpo2 = findViewById(R.id.etSpo2);
        etBodyTemp = findViewById(R.id.etBodyTemp);
        etPulse = findViewById(R.id.etPulse);
        etSysBp = findViewById(R.id.etSysBP);
        etDiaBp = findViewById(R.id.etDiaBP);
        etGFasting = findViewById(R.id.etGFasting);
        etGRandom = findViewById(R.id.etGRandom);
        addPatient = findViewById(R.id.btnAddPatient);
        submit = findViewById(R.id.submit_id);

        add_bed = findViewById(R.id.btnAddBed);
        bedMenu = findViewById(R.id.bedMenuView);

        String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());


        SharedPreferences userDetails = getSharedPreferences("userDetails", MODE_PRIVATE);
        String pin = userDetails.getString("pin", "");
        String hospital = userDetails.getString("hospital", "");

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference(pin).child(hospital);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<String> list = new ArrayList<>();
                list.add("Choose Bed");
                for (DataSnapshot snap : snapshot.getChildren()){
                    list.add(snap.getKey());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(NurseDataActivity.this, android.R.layout.select_dialog_item,list);
                bedMenu.setThreshold(2);
                bedMenu.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                bedMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        bed_choosed = list.get(i);
                        try {
                            patientName.setText(snapshot.child(bed_choosed).child("name").getValue().toString());
                        }catch(Exception e){
                            patientName.setText("N/A");
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });



        addPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(NurseDataActivity.this, android.R.style.Theme_Dialog);
                dialog.setContentView(R.layout.add_bed_dialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                EditText name  = dialog.findViewById(R.id.editText);
                // String date_started = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());


                Button save = dialog.findViewById(R.id.button);
                Button done = dialog.findViewById(R.id.done_id);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (bed_choosed.equals("Choose Bed")){
                            Toast.makeText(NurseDataActivity.this, "Choose Bed", Toast.LENGTH_SHORT).show();
                        }else{
                            reference.child(bed_choosed).child("name").setValue(name.getText().toString());
                            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                            reference.child(bed_choosed).child("dates").child("added_on").setValue(date);

                        }



                    }
                });
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });

                name.setHint("Enter Patient Name");
            }
        });


        add_bed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(NurseDataActivity.this, android.R.style.Theme_Dialog);
                dialog.setContentView(R.layout.add_bed_dialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                EditText bed_id  = dialog.findViewById(R.id.editText);

                bed_id.setHint("Enter BED ID");
                Button save = dialog.findViewById(R.id.button);
                Button done = dialog.findViewById(R.id.done_id);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (bed_id.getText().toString()!=null){
                            reference.child(bed_id.getText().toString()).child("name").setValue("DATA NOT AVAILABLE");
                            // reference.child(pin).child(hospital).child(bed_id.getText().toString()).child(currentDate).child("spo2").setValue("DATA NOT AVAILABLE");
                            bed_id.setText("");
                        }else{
                            Toast.makeText(NurseDataActivity.this, "Enter BED ID", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ////ADD DATEE

                reference.child(bed_choosed).child("dates").child("last_updated").setValue(currentDate);

                if (etSpo2.getText().toString()!=null){

                    reference.child(bed_choosed).child("details").child(currentDate).child("spo2").setValue(etSpo2.getText().toString());
                }else{
                    reference.child(bed_choosed).child("details").child(currentDate).child("spo2").setValue("NA");

                }

                if (etBodyTemp.getText().toString()!=null){
                    reference.child(bed_choosed).child("details").child(currentDate).child("body_temp").setValue(etBodyTemp.getText().toString());
                }else{
                    reference.child(bed_choosed).child("details").child(currentDate).child("body_temp").setValue("NA");

                }

                if (etPulse.getText().toString()!=null){
                    reference.child(bed_choosed).child("details").child(currentDate).child("pulse").setValue(etPulse.getText().toString());
                }else{
                    reference.child(bed_choosed).child("details").child(currentDate).child("pulse").setValue("NA");

                }

                if (etSysBp.getText().toString()!=null){
                    reference.child(bed_choosed).child("details").child(currentDate).child("sys_bp").setValue(etSysBp.getText().toString());
                }else{
                    reference.child(bed_choosed).child("details").child(currentDate).child("sys_bp").setValue("NA");

                }

                if (etDiaBp.getText().toString()!=null){
                    reference.child(bed_choosed).child("details").child(currentDate).child("dia_bp").setValue(etDiaBp.getText().toString());
                }else{
                    reference.child(bed_choosed).child("details").child(currentDate).child("dia_bp").setValue("NA");

                }

                if (etGRandom.getText().toString()!=null){
                    reference.child(bed_choosed).child("details").child(currentDate).child("g_random").setValue(etGRandom.getText().toString());
                }else{
                    reference.child(bed_choosed).child("details").child(currentDate).child("g_random").setValue("NA");

                }

                if (etGFasting.getText().toString()!=null){
                    reference.child(bed_choosed).child("details").child(currentDate).child("g_fasting").setValue(etGFasting.getText().toString());
                }else{
                    reference.child(bed_choosed).child("details").child(currentDate).child("g_fasting").setValue("NA");

                }

                startActivity(new Intent(NurseDataActivity.this,NurseDataActivity.class));

            }
        });


    }
}