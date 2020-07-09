package com.andorid.go_bengkel.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.model.UserAppModel;
import com.andorid.go_bengkel.preference.AppPreference;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditBengkelActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private Button buttonSimpan;
    private TextInputEditText textInputEditTextNamaPemilikBengkel;
    private TextInputEditText textInputEditTextNamaBengkel;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextTelepon;
    private TextInputEditText textInputEditTextJamBuka;
    private TextInputEditText textInputEditTextJamTutup;
    private TextInputEditText textInputEditTextAlamat;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextKonfirmasiPassword;

    private UserAppModel model;
    private String latitude, longitude;
    private boolean checkEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bengkel);

        model = AppPreference.getUser(this);
        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");

        textInputEditTextNamaPemilikBengkel = findViewById(R.id.textInputEditTextNamaPemilikBengkel);
        textInputEditTextNamaBengkel = findViewById(R.id.textInputEditTextNamaBengkel);
        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
        textInputEditTextTelepon = findViewById(R.id.textInputEditTextTelepon);
        textInputEditTextJamBuka = findViewById(R.id.textInputEditTextJamBuka);
        textInputEditTextJamTutup = findViewById(R.id.textInputEditTextJamTutup);
        textInputEditTextAlamat = findViewById(R.id.textInputEditTextAlamat);
        textInputEditTextPassword = findViewById(R.id.textInputEditTextPassword);
        textInputEditTextKonfirmasiPassword = findViewById(R.id.textInputEditTextKonfirmasiPassword);
        buttonSimpan = findViewById(R.id.buttonSimpan);

        getAccountFirebase();

        textInputEditTextAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditBengkelActivity.this)
                        .setItems(new String[]{"Tulis alamat", "Pilih dari peta"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // Set up the input
                                        final EditText input = new EditText(EditBengkelActivity.this);
                                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

                                        new AlertDialog.Builder(EditBengkelActivity.this)
                                                .setView(input)
                                                .setTitle("Masukkan Alamat Bengkel")
                                                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        textInputEditTextAlamat.setText(input.getText().toString());
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
                                        Intent i = new Intent(EditBengkelActivity.this, LocationPickerActivity.class);
                                        startActivityForResult(i, 99);
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });

        buttonSimpan.setOnClickListener(new View.OnClickListener() {
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

                if (textInputEditTextNamaPemilikBengkel.getText().toString().matches("")){
                    textInputEditTextNamaPemilikBengkel.setError("Data tidak boleh kosong");
                    cek1 = false;
                }
                if (textInputEditTextNamaBengkel.getText().toString().matches("")){
                    textInputEditTextNamaBengkel.setError("Data tidak boleh kosong");
                    cek2 = false;
                }
                if (textInputEditTextEmail.getText().toString().matches("")){
                    textInputEditTextEmail.setError("Data tidak boleh kosong");
                    cek3 = false;
                }
                if (textInputEditTextTelepon.getText().toString().matches("")){
                    textInputEditTextTelepon.setError("Data tidak boleh kosong");
                    cek4 = false;
                }
                if (textInputEditTextJamBuka.getText().toString().matches("")){
                    textInputEditTextJamBuka.setError("Data tidak boleh kosong");
                    cek5 = false;
                }
                if (textInputEditTextJamTutup.getText().toString().matches("")){
                    textInputEditTextJamTutup.setError("Data tidak boleh kosong");
                    cek6 = false;
                }
                if (textInputEditTextAlamat.getText().toString().matches("")){
                    textInputEditTextAlamat.setError("Data tidak boleh kosong");
                    cek7 = false;
                }
                if (textInputEditTextPassword.getText().toString().matches("")){
                    textInputEditTextPassword.setError("Data tidak boleh kosong");
                    cek8 = false;
                }
                if (textInputEditTextKonfirmasiPassword.getText().toString().matches("")){
                    textInputEditTextKonfirmasiPassword.setError("Data tidak boleh kosong");
                    cek9 = false;
                }
                if (textInputEditTextPassword.getText().toString().trim().length() > 0 && textInputEditTextKonfirmasiPassword.getText().toString().trim().length() > 0){
                    if (!textInputEditTextPassword.getText().toString().equals(textInputEditTextKonfirmasiPassword.getText().toString())) {
                        textInputEditTextPassword.setError("Kombinasi password dan konfirmasi password tidak cocok");
                        textInputEditTextKonfirmasiPassword.setError("Kombinasi password dan konfirmasi password tidak cocok");
                        cek10 = false;
                    }
                }

                if (cek1 && cek2 && cek3 && cek4 && cek5 && cek6 && cek7 && cek8 && cek9 && cek10) {
                    progressDialog.setMessage("Tunggu Sebentar...");
                    progressDialog.show();

                    getLatLong(textInputEditTextAlamat.getText().toString());
                    checkEmailFirebase();
                    int loadingTime = 3000;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (checkEmail) {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditBengkelActivity.this, "Email telah terdaftar, gunakan email lain!", Toast.LENGTH_SHORT).show();
                                    textInputEditTextEmail.setFocusable(true);
                                    checkEmail = false;
                                }
                            } else {
                                if (latitude != null && longitude != null) {
                                    updateProfile(
                                            textInputEditTextEmail.getText().toString(),
                                            textInputEditTextPassword.getText().toString(),
                                            textInputEditTextNamaPemilikBengkel.getText().toString(),
                                            textInputEditTextNamaBengkel.getText().toString(),
                                            textInputEditTextTelepon.getText().toString(),
                                            textInputEditTextJamBuka.getText().toString(),
                                            textInputEditTextJamTutup.getText().toString(),
                                            textInputEditTextAlamat.getText().toString(),
                                            latitude,
                                            longitude
                                    );
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                        checkEmail = false;
                                        Toast.makeText(EditBengkelActivity.this, "Data profil berhasil disimpan", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        //finish();
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

    public void getAccountFirebase() {
        databaseReference.child("Bengkel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    String userKey = noteDataSnapshot.getKey();
                    if (userKey.equals(model.getUserKey())) {
                        if (userKey.equals(model.getUserKey())) {
                            textInputEditTextNamaPemilikBengkel.setText(noteDataSnapshot.child("namaPemilikBengkel").getValue(String.class));
                            textInputEditTextNamaBengkel.setText(noteDataSnapshot.child("namaBengkel").getValue(String.class));
                            textInputEditTextTelepon.setText(noteDataSnapshot.child("telepon").getValue(String.class));
                            textInputEditTextJamBuka.setText(noteDataSnapshot.child("jamBuka").getValue(String.class));
                            textInputEditTextJamTutup.setText(noteDataSnapshot.child("jamTutup").getValue(String.class));
                            textInputEditTextAlamat.setText(noteDataSnapshot.child("alamat").getValue(String.class));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    String userKey = noteDataSnapshot.getKey();
                    if (userKey.equals(model.getUserKey())) {
                        if (userKey.equals(model.getUserKey())) {
                            textInputEditTextEmail.setText(noteDataSnapshot.child("email").getValue(String.class));
                            textInputEditTextPassword.setText(noteDataSnapshot.child("password").getValue(String.class));
                            textInputEditTextKonfirmasiPassword.setText(noteDataSnapshot.child("password").getValue(String.class));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateProfile(String email, String password, String namaPemilikBengkel, String namaBengkel, String telepon, String jamBuka, String jamTutup, String alamat, String latitude, String longitude) {
        databaseReference.child("User").child(model.getUserKey()).child("email").setValue(email);
        databaseReference.child("User").child(model.getUserKey()).child("password").setValue(password);
        databaseReference.child("Bengkel").child(model.getUserKey()).child("namaPemilikBengkel").setValue(namaPemilikBengkel);
        databaseReference.child("Bengkel").child(model.getUserKey()).child("namaBengkel").setValue(namaBengkel);
        databaseReference.child("Bengkel").child(model.getUserKey()).child("telepon").setValue(telepon);
        databaseReference.child("Bengkel").child(model.getUserKey()).child("jamBuka").setValue(jamBuka);
        databaseReference.child("Bengkel").child(model.getUserKey()).child("jamTutup").setValue(jamTutup);
        databaseReference.child("Bengkel").child(model.getUserKey()).child("alamat").setValue(alamat);
        databaseReference.child("Bengkel").child(model.getUserKey()).child("latitude").setValue(latitude);
        databaseReference.child("Bengkel").child(model.getUserKey()).child("longitude").setValue(longitude);
    }

    public void checkEmailFirebase() {
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    String email = noteDataSnapshot.child("email").getValue(String.class);
                    if (email != null) {
                        if (textInputEditTextEmail.getText().toString().equalsIgnoreCase(email)) {
                            if (noteDataSnapshot.getKey().equals(model.getUserKey())) {
                                checkEmail = false;
                            } else {
                                checkEmail = true;
                            }
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
                Log.e("latitude", latitude);
                Log.e("longitude", longitude);
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
                    textInputEditTextAlamat.setText(alamat);
                }
                break;
            }
        }
    }
}