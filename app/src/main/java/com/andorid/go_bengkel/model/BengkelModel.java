package com.andorid.go_bengkel.model;

import java.io.Serializable;

public class BengkelModel implements Serializable {
    private String namaPemilikBengkel;
    private String namaBengkel;
    private String telepon;
    private String jamBuka;
    private String jamTutup;
    private String alamat;
    private String latitude;
    private String longitude;

    public BengkelModel(String namaPemilikBengkel, String namaBengkel, String telepon, String jamBuka, String jamTutup, String alamat, String latitude, String longitude) {
        this.namaPemilikBengkel = namaPemilikBengkel;
        this.namaBengkel = namaBengkel;
        this.telepon = telepon;
        this.jamBuka = jamBuka;
        this.jamTutup = jamTutup;
        this.alamat = alamat;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNamaPemilikBengkel() {
        return namaPemilikBengkel;
    }

    public void setNamaPemilikBengkel(String namaPemilikBengkel) {
        this.namaPemilikBengkel = namaPemilikBengkel;
    }

    public String getNamaBengkel() {
        return namaBengkel;
    }

    public void setNamaBengkel(String namaBengkel) {
        this.namaBengkel = namaBengkel;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getJamBuka() {
        return jamBuka;
    }

    public void setJamBuka(String jamBuka) {
        this.jamBuka = jamBuka;
    }

    public String getJamTutup() {
        return jamTutup;
    }

    public void setJamTutup(String jamTutup) {
        this.jamTutup = jamTutup;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
