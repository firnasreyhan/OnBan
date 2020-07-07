package com.andorid.go_bengkel.model;

import java.io.Serializable;

public class TransaksiModel implements Serializable {
    private String pelangganId;
    private String bengkelId;
    private String latitude;
    private String longitude;
    private String alamat;
    private String rincian;
    private String tanggal;
    private String status;

    public TransaksiModel(String pelangganId, String bengkelId, String latitude, String longitude, String alamat, String rincian, String tanggal, String status) {
        this.pelangganId = pelangganId;
        this.bengkelId = bengkelId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.alamat = alamat;
        this.rincian = rincian;
        this.tanggal = tanggal;
        this.status = status;
    }

    public String getPelangganId() {
        return pelangganId;
    }

    public void setPelangganId(String pelangganId) {
        this.pelangganId = pelangganId;
    }

    public String getBengkelId() {
        return bengkelId;
    }

    public void setBengkelId(String bengkelId) {
        this.bengkelId = bengkelId;
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

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getRincian() {
        return rincian;
    }

    public void setRincian(String rincian) {
        this.rincian = rincian;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
