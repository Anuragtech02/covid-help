package com.anurag.covidhelp.Helpers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.anurag.covidhelp.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void getUser(String phone, OnUserFetched onUserFeteched){

        firestore.collection("users").whereEqualTo("contact", phone.contains("+91") ? phone : "+91"+phone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){
                                Log.d("EPT", "Empty result");
                                onUserFeteched.OnUserFetched(null);
                            }else{
                                Log.d("FND", "Result Found");
                                List<UserModel> usersList = task.getResult().toObjects(UserModel.class);
                                UserModel user = usersList.get(0);
                                onUserFeteched.OnUserFetched(user);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
            }
        });
    }

    public void addUser(String uid, String name, String designation, String pin, String hospital, Activity activity){

//        String uid = firestore.collection("users").document().getId();

        UserModel user = new UserModel(uid, name, designation, pin, hospital
                , FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());


        firestore.collection("users")
                .document(uid)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        Toast.makeText(activity, "Successfully added User", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(activity, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void isValuePresent(String collection, String field, String value, BooleanValueChecker booleanValueChecker){

        firestore.collection(collection).whereEqualTo(field, value).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty()){
                        Log.d("FIELD_VALUE", "NOT FOUND");
                        booleanValueChecker.isValuePresent(false);
                    }else{
                        Log.d("FIELD_VALUE", "FOUND");
                        booleanValueChecker.isValuePresent(true);
                    }
                }
            }
        });
    }

    public void addPin(String pin, ArrayList<String> hospitals, Activity activity){

        Map<String, Object> pinModel = new HashMap<>();

        pinModel.put("pinCode", pin);
        pinModel.put("hospitals", hospitals);

        firestore.collection("pinCodes")
                .document(pin)
                .set(pinModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        Toast.makeText(activity, "Successfully added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(activity, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void addHospital(String pin, ArrayList<String> hospitals, Activity activity){

        firestore.collection("pinCodes")
                .document(pin)
                .update("hospitals", hospitals)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        Toast.makeText(activity, "Successfully added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(activity, "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
