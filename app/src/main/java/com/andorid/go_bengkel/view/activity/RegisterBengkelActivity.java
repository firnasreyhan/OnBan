package com.andorid.go_bengkel.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.model.BengkelModel;
import com.andorid.go_bengkel.model.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RegisterBengkelActivity extends Activity {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;

    private TextInputEditText editTextNamaPemilikBengkel, editTextNamaBengkel, editTextEmail, editTextNomorTelepon, editTextJamBuka, editTextJamTutup, editTextAlamat, editTextPassword, editTextKonfirmasiPassword;
    private Button buttonDaftar;

    private String userId, latitude, longitude;
    private boolean checkEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_bengkel);

        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");
        userId = databaseReference.push().getKey();

        editTextNamaPemilikBengkel = findViewById(R.id.textInputEditTextNamaPemilikBengkel);
        editTextNamaBengkel = findViewById(R.id.textInputEditTextNamaBengkel);
        editTextEmail = findViewById(R.id.textInputEditTextEmail);
        editTextNomorTelepon = findViewById(R.id.textInputEditTextTelepon);
        editTextJamBuka = findViewById(R.id.textInputEditTextJamBuka);
        editTextJamTutup = findViewById(R.id.textInputEditTextJamTutup);
        editTextAlamat = findViewById(R.id.textInputEditTextAlamat);
        editTextPassword = findViewById(R.id.textInputEditTextPassword);
        editTextKonfirmasiPassword = findViewById(R.id.textInputEditTextKonfirmasiPassword);
        buttonDaftar = findViewById(R.id.buttonDaftar);

        editTextAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RegisterBengkelActivity.this)
                        .setItems(new String[]{"Tulis alamat", "Pilih dari peta"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // Set up the input
                                        final EditText input = new EditText(RegisterBengkelActivity.this);
                                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                                        new AlertDialog.Builder(RegisterBengkelActivity.this)
                                                .setView(input)
                                                .setTitle("Masukkan Alamat Bengkel")
                                                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        editTextAlamat.setText(input.getText().toString());
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();
                                        dialog.dismiss();
                                        break;
                                    case 1:
                                        //startActivity(new Intent(RegisterBengkelActivity.this, LocationPickerActivity.class));
                                        Intent i = new Intent(RegisterBengkelActivity.this, LocationPickerActivity.class);
                                        startActivityForResult(i, 99);
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });

        buttonDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cek1 = true;
                boolean cek2 = true;
                boolean cek3 = true;
                boolean cek4 = true;
                boolean cek5 = true;
                boolean cek6 = true;
                boolean cek7 = true;
                boolean cek8 = true;
                boolean cek9 = true;
                boolean cek10 = true;

                if (editTextNamaPemilikBengkel.getText().toString().matches("")){
                    editTextNamaPemilikBengkel.setError("Data tidak boleh kosong");
                    cek1 = false;
                }
                if (editTextNamaBengkel.getText().toString().matches("")){
                    editTextNamaBengkel.setError("Data tidak boleh kosong");
                    cek2 = false;
                }
                if (editTextEmail.getText().toString().matches("")){
                    editTextEmail.setError("Data tidak boleh kosong");
                    cek3 = false;
                }
                if (editTextNomorTelepon.getText().toString().matches("")){
                    editTextNomorTelepon.setError("Data tidak boleh kosong");
                    cek4 = false;
                }
                if (editTextJamBuka.getText().toString().matches("")){
                    editTextJamBuka.setError("Data tidak boleh kosong");
                    cek5 = false;
                }
                if (editTextJamTutup.getText().toString().matches("")){
                    editTextJamTutup.setError("Data tidak boleh kosong");
                    cek6 = false;
                }
                if (editTextAlamat.getText().toString().matches("")){
                    editTextAlamat.setError("Data tidak boleh kosong");
                    cek7 = false;
                }
                if (editTextPassword.getText().toString().matches("")){
                    editTextPassword.setError("Data tidak boleh kosong");
                    cek8 = false;
                }
                if (editTextKonfirmasiPassword.getText().toString().matches("")){
                    editTextKonfirmasiPassword.setError("Data tidak boleh kosong");
                    cek9 = false;
                }
                if (editTextPassword.getText().toString().trim().length() > 0 && editTextKonfirmasiPassword.getText().toString().trim().length() > 0){
                    if (!editTextPassword.getText().toString().equals(editTextKonfirmasiPassword.getText().toString())) {
                        editTextPassword.setError("Kombinasi password dan konfirmasi password tidak cocok");
                        editTextKonfirmasiPassword.setError("Kombinasi password dan konfirmasi password tidak cocok");
                        cek10 = false;
                    }
                }

                if (cek1 && cek2 && cek3 && cek4 && cek5 && cek6 && cek7 && cek8 && cek9 && cek10) {
                    progressDialog.setMessage("Tunggu Sebentar...");
                    progressDialog.show();

                    getLatLong(editTextAlamat.getText().toString());
                    checkEmailFirebase();
                    int loadingTime = 3000;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (checkEmail) {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterBengkelActivity.this, "Email telah terdaftar, gunakan email lain!", Toast.LENGTH_SHORT).show();
                                    editTextEmail.setFocusable(true);
                                    checkEmail = false;
                                }
                            } else {
                                if (latitude != null && longitude != null) {
                                    addUser(
                                            editTextEmail.getText().toString(),
                                            editTextPassword.getText().toString(),
                                            "Bengkel",
                                            editTextNamaPemilikBengkel.getText().toString(),
                                            editTextNamaBengkel.getText().toString(),
                                            editTextNomorTelepon.getText().toString(),
                                            editTextJamBuka.getText().toString(),
                                            editTextJamTutup.getText().toString(),
                                            editTextAlamat.getText().toString(),
                                            latitude,
                                            longitude
                                    );
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterBengkelActivity.this, "Registrasi berhasil, anda dapat masuk kedalam aplikasi", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                } else {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    new AlertDialog.Builder(v.getContext())
                                            .setMessage("Lokasi bengkel tidak ditemukan, mohon gunakan alamat yang lain")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            }
                        }
                    }, loadingTime);
                }
            }
        });
    }

    public void addUser(String email, String password, String status, String namaPemilikBengkel, String namaBengkel, String telepon, String jamBuka, String jamTutup, String alamat, String latitude, String longitude) {
        UserModel model = new UserModel(email, password, status);
        databaseReference.child("User").child(userId).setValue(model);
        addBengkel(namaPemilikBengkel, namaBengkel, telepon, jamBuka, jamTutup, alamat, latitude, longitude);
    }

    public void addBengkel(String namaPemilikBengkel, String namaBengkel, String telepon, String jamBuka, String jamTutup, String alamat, String latitude, String longitude) {
        BengkelModel model = new BengkelModel(namaPemilikBengkel, namaBengkel, telepon, jamBuka, jamTutup, alamat, latitude, longitude);
        databaseReference.child("Bengkel").child(userId).setValue(model);
    }

    public void checkEmailFirebase() {
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    String email = noteDataSnapshot.child("email").getValue(String.class);
                    if (email != null) {
                        if (editTextEmail.getText().toString().equalsIgnoreCase(email)) {
                            checkEmail = true;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getLatLong(String locationAddress) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List addressList = geocoder.getFromLocationName(locationAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = (Address) addressList.get(0);
                latitude = String.valueOf(address.getLatitude());
                longitude = String.valueOf(address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (99) : {
                if (resultCode == Activity.RESULT_OK) {
                    String alamat = data.getStringExtra("alamat");
                    String latitude = data.getStringExtra("latitude");
                    String longitude = data.getStringExtra("longitude");
                    this.latitude = latitude;
                    this.longitude = longitude;
                    editTextAlamat.setText(alamat);
                }
                break;
            }
        }
    }
}