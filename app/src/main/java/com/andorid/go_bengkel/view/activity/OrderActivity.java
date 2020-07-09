package com.andorid.go_bengkel.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.model.ReviewModel;
import com.andorid.go_bengkel.model.TransaksiModel;
import com.andorid.go_bengkel.model.UserAppModel;
import com.andorid.go_bengkel.preference.AppPreference;
import com.andorid.go_bengkel.service.APIService;
import com.andorid.go_bengkel.service.Client;
import com.andorid.go_bengkel.service.Data;
import com.andorid.go_bengkel.service.MyResponse;
import com.andorid.go_bengkel.service.NotificationSender;
import com.andorid.go_bengkel.service.Token;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private APIService apiService;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    private TextView textViewLokasi, textViewTanggal;
    private EditText editTextRincian;
    private Button buttonPesanSekarang;

    private UserAppModel userAppModel;

    private String date, bengkelId, latitude, longitude, transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setTitle("Pesan Layanan");

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        userAppModel = AppPreference.getUser(this);
        bengkelId = getIntent().getStringExtra("bengkelId");
        progressDialog = new ProgressDialog(this);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");
        transactionId = databaseReference.push().getKey();

        textViewLokasi = findViewById(R.id.textViewLokasi);
        textViewTanggal = findViewById(R.id.textViewTanggal);
        editTextRincian = findViewById(R.id.editTextRincian);
        buttonPesanSekarang = findViewById(R.id.buttonPesanSekarang);

        date = dateFormat.format(calendar.getTime());
        textViewTanggal.setText(date);

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    OrderActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getLocation();
        }

        buttonPesanSekarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(OrderActivity.this)
                        .setTitle("Informasi")
                        .setMessage("Transaksi sedang menunggu konfirmasi. \nHarap memantau pesanan pada halaman Riwayat dan tunggu montir untuk sampai ke lokasi Anda.")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("OnBan").child("Token").child(bengkelId).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String usertoken = dataSnapshot.getValue(String.class);
                                        sendNotifications(usertoken, "Ada pesanan baru dari pelanggan!", "Silahkan cek aplikasi Anda.");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                addTransaction(
                                        userAppModel.getUserKey(),
                                        bengkelId,
                                        latitude,
                                        longitude,
                                        textViewLokasi.getText().toString(),
                                        editTextRincian.getText().toString(),
                                        textViewTanggal.getText().toString()
                                );
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addTransaction(String pelangganId, String bengkelId, String latitude, String longitude, String alamat, String rincian, String tanggal) {
        TransaksiModel model = new TransaksiModel(
                pelangganId,
                bengkelId,
                latitude,
                longitude,
                alamat,
                rincian,
                tanggal,
                "Menunggu Konfirmasi"
        );

        ReviewModel reviewModel = new ReviewModel(
                "0",
                "-"
        );
        databaseReference.child("Transaksi").child(transactionId).setValue(model);
        databaseReference.child("Review").child(transactionId).setValue(reviewModel);
    }

    public void getLocation() {
        progressDialog.setMessage("Tunggu Sebentar...");
        progressDialog.show();

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    OrderActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            LocationServices.getFusedLocationProviderClient(OrderActivity.this)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(OrderActivity.this)
                                    .removeLocationUpdates(this);
                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                int latestLocationIndex = locationResult.getLocations().size() - 1;
                                double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                                getAddress(latitude, longitude);
                            }
                        }
                    }, Looper.getMainLooper());
        }
    }

    public void getAddress(double latitude, double longitude) {
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(OrderActivity.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            textViewLokasi.setText(address);

            if (address != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(OrderActivity.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}