package com.anurag.covidhelp.Models;

public class UserModel {
    public String uid;
    public String name;
    public String designation;
    public String pinCode;
    public String hospital;
    public String contact;

    public UserModel(String uid, String name, String designation, String pinCode, String hospital, String contact) {
        this.uid = uid;
        this.name = name;
        this.designation = designation;
        this.pinCode = pinCode;
        this.hospital = hospital;
        this.contact = contact;
    }
    public UserModel(){

    }
}
