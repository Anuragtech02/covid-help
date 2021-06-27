package com.anurag.covidhelp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.covidhelp.Adapters.RecentPatientRVAdapter;
import com.anurag.covidhelp.Models.PatientModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    RecyclerView rvRecentPatients;
    RecentPatientRVAdapter rvAdapter;

    private FirebaseUser mCurrentUser;

    private ImageButton accBtn;
    private TextView mName;
    private FloatingActionButton mAddDetailsFab;

    private SharedPreferences userDetails;

    private AutoCompleteTextView bed_sugegestion;

    private ArrayList<PatientModel> patients;

    private ConstraintLayout dashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = Dashboard.this;


        // Changing status bar color
        Window window = activity.getWindow();
        Drawable background = activity.getResources().getDrawable(R.drawable.status_gradient);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);

        setContentView(R.layout.activity_dashboard);

        rvRecentPatients = findViewById(R.id.rvPatients);

        //AUTOCOMPLETE TEXTVIEW
        bed_sugegestion = findViewById(R.id.searchView);

//        dashboard = findViewById(R.id.dashboard);

        getWindow().setBackgroundDrawableResource(R.mipmap.dashboard);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        userDetails = getSharedPreferences("userDetails", MODE_PRIVATE);

        //AUTOCOMPLETE TEXTVIEW DATABASE
        String user_pin = null, user_hospital = null;

        user_pin = userDetails.getString("pin", "Doctor");
        user_hospital = userDetails.getString("hospital", "Doctor");

//        bed_sugegestion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Dashboard.this, SearchActivity.class);
////                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Dashboard.this,
////                        v,   // Starting view
////                        "searchTransition"    // The String
////                );
////
////                ActivityCompat.startActivity(Dashboard.this, intent, options.toBundle());
////                startActivity(intent);
//            }
//        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("452001").child("Gokuldas");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<String> list = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    list.add(snap.getKey());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Dashboard.this, android.R.layout.select_dialog_item, list);
                bed_sugegestion.setThreshold(2);
                bed_sugegestion.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                bed_sugegestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(Dashboard.this, SearchActivity.class);
                        intent.putExtra("bedId", list.get(i));
                        intent.putExtra("beds", list);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        accBtn = findViewById(R.id.accBtn);
        mName = findViewById(R.id.name);
        mAddDetailsFab = findViewById(R.id.fabAddDetails);

        patients = new ArrayList<>();
        PatientModel patient1 = new PatientModel("", "Anurag", "BedXYZ", "Apple Hospital",
                98, 40, 120, 0, 97, 120, 39);
        PatientModel patient2 = new PatientModel("", "Naman", "BedABC", "Govt Hospital",
                96, 42, 121, 20, 98, 130, 38);
        PatientModel patient3 = new PatientModel("", "Test", "BedIJK", "Bombay Hospital",
                92, 40, 118, 22, 96, 135, 40);

        patients.add(patient1);
        patients.add(patient2);
        patients.add(patient3);

        Log.d("Patients", patients.toString());

        rvRecentPatients.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        rvAdapter = new RecentPatientRVAdapter(this, patients);
        rvRecentPatients.setAdapter(rvAdapter);
        rvAdapter.setItemClickListener(onItemClickListener);

//        if(userDetails.contains("name")){
        String name = userDetails.getString("name", "Doctor");
        if (!name.isEmpty()) {
            mName.setText(name);
        }
//        }

        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Dashboard.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(Dashboard.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton("Cancel", null);

                builder.show();
            }
        });

        mAddDetailsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Yes it works", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Dashboard.this, NurseDataActivity.class);
                startActivity(intent);
            }
        });
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            PatientModel patient = patients.get(position);
            Intent intent = new Intent(Dashboard.this, PatientDetails.class);
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

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser == null){
            Intent intent = new Intent(Dashboard.this, Login.class);
            startActivity(intent);
            finish();
        }
    }
}