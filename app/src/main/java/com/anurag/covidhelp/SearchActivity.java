package com.anurag.covidhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.anurag.covidhelp.Adapters.PatientCardRVAdapter;
import com.anurag.covidhelp.Adapters.RecentPatientRVAdapter;
import com.anurag.covidhelp.Models.PatientModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;


import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView rvBeds;
    private ArrayList<PatientModel> patients;
    private PatientCardRVAdapter rvAdapter;
    private TextView name ;
    private AutoCompleteTextView searchMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        rvBeds = findViewById(R.id.rvBeds);
        searchMenu = findViewById(R.id.searchView);
        name = findViewById(R.id.bedsHeading);

        //FIREBASE DATABASE

        patients = new ArrayList<>();
      /* PatientModel patient1 = new PatientModel("Anurag", "BedXYZ", "Apple Hospital",
                98, 40, 120, 0, 97, 120, 39);
        PatientModel patient2 = new PatientModel("Naman", "BedABC", "Govt Hospital",
                96, 42, 121, 20, 98, 130, 38);
        PatientModel patient3 = new PatientModel("Test", "BedIJK", "Bombay Hospital",
                92, 40, 118, 22, 96, 135, 40);

        patients.add(patient1);
        patients.add(patient2);
        patients.add(patient3);
*/
        Log.d("Patients", patients.toString());

        rvBeds.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter = new PatientCardRVAdapter(this, patients);
        rvBeds.setAdapter(rvAdapter);
        rvAdapter.setItemClickListener(onItemClickListener);

        Intent intent = getIntent();

        String bedId = intent.getStringExtra("bedId");
        searchMenu.setText(bedId);

        ArrayList<String> beds = intent.getStringArrayListExtra("beds");

        SharedPreferences userDetails = getSharedPreferences("userDetails", MODE_PRIVATE);
        String pin = userDetails.getString("pin", "");
        String hospital = userDetails.getString("hospital", "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(pin).child(hospital);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<String> list = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()){
                    list.add(snap.getKey());

                    for (DataSnapshot snap1 : snap.child("details").getChildren()){
                        String n= snap.child("name").getValue().toString();
                        name.setText(n);
                        String date = snap1.getKey();
                        PatientModel patientmodel = new PatientModel(date,
                                n,bedId,
                                "Bombay Hospital",
                                Integer.parseInt(snap1.child("spo2").getValue().toString()),
                                Integer.parseInt(snap1.child("dia_bp").getValue().toString()),
                                Integer.parseInt(snap1.child("g_fasting").getValue().toString()),
                                Integer.parseInt(snap1.child("g_random").getValue().toString()),
                                Integer.parseInt(snap1.child("pulse").getValue().toString()),
                                Integer.parseInt(snap1.child("sys_bp").getValue().toString()),
                                Integer.parseInt(snap1.child("body_temp").getValue().toString()));

                        patients.add(patientmodel);
                    }

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.select_dialog_item,list);
                searchMenu.setThreshold(2);
                searchMenu.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                searchMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(SearchActivity.this,PatientDetails.class);
                        intent.putExtra("id",list.get(i));
                        startActivity(intent);

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            PatientModel patient = patients.get(position);
            Intent intent = new Intent(SearchActivity.this, PatientDetails.class);
            intent.putExtra("date", patient.date);
            intent.putExtra("patientName", patient.name);
            intent.putExtra("patientBed", patient.bed);
            intent.putExtra("spo2", patient.spo2);
            intent.putExtra("pulse", patient.pulse);
            intent.putExtra("sysBp", patient.sysBp);
            intent.putExtra("diaBp", patient.diaBp);
            intent.putExtra("glucoFast", patient.glucoFast);
            intent.putExtra("glucoRandom", patient.glucoRandom);
            startActivity(intent);
        }
    };
}