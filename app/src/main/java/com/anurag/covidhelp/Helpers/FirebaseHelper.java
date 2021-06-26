package com.anurag.covidhelp.Helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseHelper {
    private boolean checkStatus;
    FirebaseFirestore firestore;

    public FirebaseHelper() {
//        checkStatus = false;
        super();
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void isUserRegistered(String phone, OnCheckRegistered callback){

        firestore.collection("users").whereEqualTo("contact", phone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){
                                Log.d("EPT", "Empty result");
                                callback.onCheckRegistered(false);
                            }else{
                                Log.d("FND", "Result Found");
                                callback.onCheckRegistered(true);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
            }
        });

    }

}
