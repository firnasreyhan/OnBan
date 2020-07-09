package com.andorid.go_bengkel.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.model.UserAppModel;
import com.andorid.go_bengkel.preference.AppPreference;
import com.andorid.go_bengkel.service.Token;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;

    private TextInputEditText editTextEmail, editTextPassword;
    private TextView textViewRegisterPelanggan, textViewRegisterBengkel, textViewLupaPassword;
    private Button buttonMasuk;

    private boolean checkAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");

        editTextEmail = findViewById(R.id.textInputEditTextEmail);
        editTextPassword = findViewById(R.id.textInputEditTextPassword);
        textViewRegisterPelanggan = findViewById(R.id.textViewRegisterPelanggan);
        textViewRegisterBengkel = findViewById(R.id.textViewRegisterBengkel);
        textViewLupaPassword = findViewById(R.id.textViewLupaPassword);
        buttonMasuk = findViewById(R.id.buttonMasuk);

        textViewRegisterPelanggan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RegisterPelangganActivity.class));
            }
        });

        textViewRegisterBengkel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), RegisterBengkelActivity.class));
            }
        });

        textViewLupaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setMessage("Harap menghubungi Admin, melalui \n email : etervibes@gmail.com \n whatsapp : +6289637136936")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        buttonMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cek1 = true;
                boolean cek2 = true;

                if (editTextEmail.getText().toString().matches("")){
                    editTextEmail.setError("Data tidak boleh kosong");
                    cek1 = false;
                }
                if (editTextPassword.getText().toString().matches("")){
                    editTextPassword.setError("Data tidak boleh kosong");
                    cek2 = false;
                }
                if (cek1 && cek2) {
                    progressDialog.setMessage("Tunggu Sebentar...");
                    progressDialog.show();

                    checkAccountFirebase();
                    int loadingTime = 3000;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (checkAccount) {
                                progressDialog.dismiss();
                                checkAccount = false;
                                String refreshToken= FirebaseInstanceId.getInstance().getToken();
                                updateToken(refreshToken);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finishAffinity();
                            } else {
                                progressDialog.dismiss();
                                checkAccount = false;
                                Toast.makeText(LoginActivity.this, "Kombinasi email dan password anda salah, silahkan coba lagi.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, loadingTime);
                }
            }
        });
    }

    public void checkAccountFirebase() {
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    String userKey = noteDataSnapshot.getKey();
                    String email = noteDataSnapshot.child("email").getValue(String.class);
                    String password = noteDataSnapshot.child("password").getValue(String.class);
                    String status = noteDataSnapshot.child("status").getValue(String.class);
                    if (email != null && password != null) {
                        if (editTextEmail.getText().toString().equalsIgnoreCase(email) && editTextPassword.getText().toString().equalsIgnoreCase(password) ) {
                            AppPreference.saveUser(LoginActivity.this, new UserAppModel(userKey, email, status));
                            checkAccount = true;
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

    private void updateToken(String refreshToken) {
        String userKey = AppPreference.getUser(getApplicationContext()).getUserKey();
        Token token = new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("OnBan").child("Token").child(userKey).setValue(token);
    }
}