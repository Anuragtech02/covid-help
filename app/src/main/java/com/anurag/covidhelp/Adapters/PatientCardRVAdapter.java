package com.anurag.covidhelp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anurag.covidhelp.Models.PatientModel;
import com.anurag.covidhelp.R;
import com.google.firebase.database.annotations.NotNull;


import java.util.ArrayList;

public class PatientCardRVAdapter extends RecyclerView.Adapter<PatientCardRVAdapter.PatientCardView> {

    private ArrayList<PatientModel> patients;
    private LayoutInflater layoutInflater;
    private View.OnClickListener onItemClickListener;

    public PatientCardRVAdapter(Context context, ArrayList<PatientModel> patients) {
        this.layoutInflater = LayoutInflater.from(context);
        this.patients = patients;
//        Log.d("PATIENTS", patients.size() + patients.get(0).name);
    }

    @NonNull
    @Override
    public PatientCardView onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.patient_card, parent, false);
        return new PatientCardView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PatientCardView holder, int position) {
        PatientModel currPatient = patients.get(position);
        Log.d("PDETAILS", currPatient.name);
//        Log.d("CURR_PATIENT", currPatient.toString());
        holder.spo2.setText(String.valueOf(currPatient.spo2));
        holder.patientName.setText(String.valueOf(currPatient.date));
        holder.bed.setText(String.valueOf("Body Temperature"));
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    public class PatientCardView extends RecyclerView.ViewHolder {

        TextView spo2, patientName, bed;

        public PatientCardView(@NonNull @NotNull View itemView) {
            super(itemView);
            spo2 = itemView.findViewById(R.id.spo2Amt);
            patientName = itemView.findViewById(R.id.patientName);
            bed = itemView.findViewById(R.id.patientBed);
            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }

    }
}

