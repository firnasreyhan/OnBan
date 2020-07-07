package com.andorid.go_bengkel.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.model.PelangganModel;
import com.andorid.go_bengkel.model.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterPelangganActivity extends Activity {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;

    private TextInputEditText editTextNama, editTextEmail, editTextNomorTelepon, editTextPassword, editTextKonfirmasiPassword;
    private Button buttonDaftar;

    private String userId;
    private boolean checkEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pelanggan);

        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");
        userId = databaseReference.push().getKey();

        editTextNama = findViewById(R.id.textInputEditTextNama);
        editTextEmail = findViewById(R.id.textInputEditTextEmail);
        editTextNomorTelepon = findViewById(R.id.textInputEditTextTelepon);
        editTextPassword = findViewById(R.id.textInputEditTextPassword);
        editTextKonfirmasiPassword = findViewById(R.id.textInputEditTextKonfirmasiPassword);
        buttonDaftar = findViewById(R.id.buttonDaftar);

        buttonDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cek1 = true;
                boolean cek2 = true;
                boolean cek3 = true;
                boolean cek4 = true;
                boolean cek5 = true;
                boolean cek6 = true;

                if (editTextNama.getText().toString().matches("")){
                    editTextNama.setError("Data tidak boleh kosong");
                    cek1 = false;
                }
                if (editTextEmail.getText().toString().matches("")){
                    editTextEmail.setError("Data tidak boleh kosong");
                    cek2 = false;
                }
                if (editTextNomorTelepon.getText().toString().matches("")){
                    editTextNomorTelepon.setError("Data tidak boleh kosong");
                    cek3 = false;
                }
                if (editTextPassword.getText().toString().matches("")){
                    editTextPassword.setError("Data tidak boleh kosong");
                    cek4 = false;
                }
                if (editTextKonfirmasiPassword.getText().toString().matches("")){
                    editTextKonfirmasiPassword.setError("Data tidak boleh kosong");
                    cek5 = false;
                }
                if (editTextPassword.getText().toString().trim().length() > 0 && editTextKonfirmasiPassword.getText().toString().trim().length() > 0){
                    if (!editTextPassword.getText().toString().equals(editTextKonfirmasiPassword.getText().toString())) {
                        editTextPassword.setError("Kombinasi password dan konfirmasi password tidak cocok");
                        editTextKonfirmasiPassword.setError("Kombinasi password dan konfirmasi password tidak cocok");
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
                                    Toast.makeText(RegisterPelangganActivity.this, "Email telah terdaftar, gunakan email lain!", Toast.LENGTH_SHORT).show();
                                    editTextEmail.setFocusable(true);
                                    checkEmail = false;
                                }
                            } else {
                                addUser(
                                        editTextEmail.getText().toString(),
                                        editTextPassword.getText().toString(),
                                        "Pelanggan",
                                        editTextNama.getText().toString(),
                                        editTextNomorTelepon.getText().toString()
                                );
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterPelangganActivity.this, "Registrasi berhasil, anda dapat masuk kedalam aplikasi", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }
                    }, loadingTime);
                }
            }
        });
    }

    public void addUser(String email, String password, String status, String nama, String telepon) {
        UserModel model = new UserModel(email, password, status);
        databaseReference.child("User").child(userId).setValue(model);
        addPelanggan(nama, telepon);
    }

    public void addPelanggan(String nama, String telepon) {
        PelangganModel model = new PelangganModel(nama, telepon);
        databaseReference.child("Pelanggan").child(userId).setValue(model);
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
}