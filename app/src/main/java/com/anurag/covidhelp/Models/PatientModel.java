package com.anurag.covidhelp.Models;

public class PatientModel {
    public String date;
    public String name;
    public String bed;
    public String hospital;
    public int spo2;
    public int diaBp;
    public int glucoFast;
    public int glucoRandom;
    public int pulse;
    public int sysBp;
    public int temp;

    public PatientModel(String date ,String name, String bed, String hospital, int spo2, int diaBp, int glucoFast, int glucoRandom, int pulse, int sysBp, int temp) {
        this.date = date ;
        this.name = name;
        this.bed = bed;
        this.hospital = hospital;
        this.spo2 = spo2;
        this.diaBp = diaBp;
        this.glucoFast = glucoFast;
        this.glucoRandom = glucoRandom;
        this.pulse = pulse;
        this.sysBp = sysBp;
        this.temp = temp;
    }

    public PatientModel(String name, String bed, String hospital, int spo2){
        this.name = name;
        this.bed = bed;
        this.hospital = hospital;
        this.spo2 = spo2;
    }
}
