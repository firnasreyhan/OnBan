package com.andorid.go_bengkel.model;

public class PelangganModel {
    private String nama;
    private String telepon;

    public PelangganModel(String nama, String telepon) {
        this.nama = nama;
        this.telepon = telepon;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }
}
