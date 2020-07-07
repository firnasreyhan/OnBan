package com.andorid.go_bengkel.model;

import java.io.Serializable;

public class DetailBengkelModel extends BengkelModel implements Serializable {
    private String userKey;
    private double jarak;

    public DetailBengkelModel(String namaPemilikBengkel, String namaBengkel, String telepon, String jamBuka, String jamTutup, String alamat, String latitude, String longitude, String userKey, double jarak) {
        super(namaPemilikBengkel, namaBengkel, telepon, jamBuka, jamTutup, alamat, latitude, longitude);
        this.userKey = userKey;
        this.jarak = jarak;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public double getJarak() {
        return jarak;
    }

    public void setJarak(double jarak) {
        this.jarak = jarak;
    }
}
