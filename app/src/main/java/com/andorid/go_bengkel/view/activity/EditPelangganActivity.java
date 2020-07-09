package com.andorid.go_bengkel.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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

public class EditPelangganActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private Button buttonSimpan;
    private TextInputEditText textInputEditTextNama;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextTelepon;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextKonfirmasiPassword;

    private UserAppModel model;
    private boolean checkEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pelanggan);

        model = AppPreference.getUser(this);
        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");

        textInputEditTextNama = findViewById(R.id.textInputEditTextNama);
        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
        textInputEditTextTelepon = findViewById(R.id.textInputEditTextTelepon);
        textInputEditTextPassword = findViewById(R.id.textInputEditTextPassword);
        textInputEditTextKonfirmasiPassword = findViewById(R.id.textInputEditTextKonfirmasiPassword);
        buttonSimpan = findViewById(R.id.buttonSimpan);

        getAccountFirebase();

        buttonSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cek1 = true;
                boolean cek2 = true;
                boolean cek3 = true;
                boolean cek4 = true;
                boolean cek5 = true;
                boolean cek6 = true;

                if (textInputEditTextNama.getText().toString().matches("")){
                    textInputEditTextNama.setError("Data tidak boleh kosong");
                    cek1 = false;
                }
                if (textInputEditTextEmail.getText().toString().matches("")){
                    textInputEditTextEmail.setError("Data tidak boleh kosong");
                    cek2 = false;
                }
                if (textInputEditTextTelepon.getText().toString().matches("")){
                    textInputEditTextTelepon.setError("Data tidak boleh kosong");
                    cek3 = false;
                }
                if (textInputEditTextPassword.getText().toString().matches("")){
                    textInputEditTextPassword.setError("Data tidak boleh kosong");
                    cek4 = false;
                }
                if (textInputEditTextKonfirmasiPassword.getText().toString().matches("")){
                    textInputEditTextKonfirmasiPassword.setError("Data tidak boleh kosong");
                    cek5 = false;
                }
                if (textInputEditTextPassword.getText().toString().trim().length() > 0 && textInputEditTextKonfirmasiPassword.getText().toString().trim().length() > 0){
                    if (!textInputEditTextPassword.getText().toString().equals(textInputEditTextKonfirmasiPassword.getText().toString())) {
                        textInputEditTextPassword.setError("Kombinasi password dan konfirmasi password tidak cocok");
                        textInputEditTextKonfirmasiPassword.setError("Kombinasi password dan konfirmasi password tidak cocok");
                        cek6 = false;
                    }
                }

                if (cek1 && cek2 && cek3 && cek4 && cek5 && cek6) {
                    progressDialog.setMessage("Tunggu Sebentar...");
                    progressDialog.show();

                    checkEmailFirebase();
                    int loadingTime = 3000;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (checkEmail) {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditPelangganActivity.this, "Email telah terdaftar, gunakan email lain!", Toast.LENGTH_SHORT).show();
                                    textInputEditTextEmail.setFocusable(true);
                                    checkEmail = false;
                                }
                            } else {
                                updateProfile(
                                        textInputEditTextEmail.getText().toString(),
                                        textInputEditTextPassword.getText().toString(),
                                        textInputEditTextNama.getText().toString(),
                                        textInputEditTextTelepon.getText().toString()
                                );
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditPelangganActivity.this, "Data profil berhasil disimpan", Toast.LENGTH_SHORT).show();
                                    checkEmail = false;
                                    finish();
                                }
                            }
                        }
                    }, loadingTime);
                }
            }
        });
    }

    public void getAccountFirebase() {
        databaseReference.child("Pelanggan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    String userKey = noteDataSnapshot.getKey();
                    if (userKey.equals(model.getUserKey())) {
                        if (userKey.equals(model.getUserKey())) {
                            textInputEditTextNama.setText(noteDataSnapshot.child("nama").getValue(String.class));
                            textInputEditTextTelepon.setText(noteDataSnapshot.child("telepon").getValue(String.class));
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

    public void updateProfile(String email, String password, String nama, String telepon) {
        databaseReference.child("User").child(model.getUserKey()).child("email").setValue(email);
        databaseReference.child("User").child(model.getUserKey()).child("password").setValue(password);
        databaseReference.child("Pelanggan").child(model.getUserKey()).child("nama").setValue(nama);
        databaseReference.child("Pelanggan").child(model.getUserKey()).child("telepon").setValue(telepon);
    }
}