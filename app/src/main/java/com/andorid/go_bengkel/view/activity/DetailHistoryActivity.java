package com.andorid.go_bengkel.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.model.TransaksiModel;
import com.andorid.go_bengkel.model.UserAppModel;
import com.andorid.go_bengkel.preference.AppPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailHistoryActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private CardView cardViewPelanggan, cardViewBengkel;
    private LinearLayout linearLayoutUlasan;
    private TextView textViewNamaBengkel, textViewLokasiKendaraan, textViewRincian, textViewTanggal, textViewStatus, textViewNamaPelanggan, textViewUlasan;
    private TextView textViewNamaPelangganBengkel, textViewLokasiKendaraanBengkel, textViewRincianBengkel, textViewTanggalBengkel, textViewStatusBengkel, textViewUlasanBengkel;
    private Button buttonTulisReview;
    private RatingBar ratingBarReview;
    private RatingBar ratingBarBengkel;

    private UserAppModel userAppModel;
    private TransaksiModel transaksiModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);
        setTitle("Detail Riwayat");

        userAppModel = AppPreference.getUser(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");
        transaksiModel = (TransaksiModel) getIntent().getSerializableExtra("transaksi");

        cardViewPelanggan = findViewById(R.id.cardViewPelanggan);
        textViewNamaBengkel = findViewById(R.id.textViewNamaBengkel);
        textViewLokasiKendaraan = findViewById(R.id.textViewLokasiKendaraan);
        textViewRincian = findViewById(R.id.textViewRincian);
        textViewTanggal = findViewById(R.id.textViewTanggal);
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewNamaPelanggan = findViewById(R.id.textViewNamaPelanggan);
        textViewUlasan = findViewById(R.id.textViewUlasan);
        buttonTulisReview = findViewById(R.id.buttonTulisReview);
        linearLayoutUlasan = findViewById(R.id.linearLayoutUlasan);
        ratingBarReview = findViewById(R.id.ratingBarReview);

        cardViewBengkel = findViewById(R.id.cardViewBengkel);
        textViewNamaPelangganBengkel = findViewById(R.id.textViewNamaPelangganBengkel);
        textViewLokasiKendaraanBengkel = findViewById(R.id.textViewLokasiKendaraanBengkel);
        textViewRincianBengkel = findViewById(R.id.textViewRincianBengkel);
        textViewTanggalBengkel = findViewById(R.id.textViewTanggalBengkel);
        textViewStatusBengkel = findViewById(R.id.textViewStatusBengkel);
        textViewUlasanBengkel = findViewById(R.id.textViewUlasanBengkel);
        ratingBarBengkel = findViewById(R.id.ratingBarBengkel);

        textViewLokasiKendaraan.setText(transaksiModel.getAlamat());
        textViewRincian.setText(transaksiModel.getRincian());
        textViewTanggal.setText(transaksiModel.getTanggal());
        textViewStatus.setText(transaksiModel.getStatus());

        if (userAppModel.getStatus().equalsIgnoreCase("Pelanggan")) {
            cardViewPelanggan.setVisibility(View.VISIBLE);
            cardViewBengkel.setVisibility(View.GONE);
            databaseReference.child("Bengkel").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        if (transaksiModel.getBengkelId().equals(noteDataSnapshot.getKey())) {
                            textViewNamaBengkel.setText(noteDataSnapshot.child("namaBengkel").getValue(String.class));
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference.child("Review").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        if (noteDataSnapshot.getKey().equals(transaksiModel.getPelangganId())) {
                            if (transaksiModel.getStatus().equalsIgnoreCase("Selesai")) {
                                if (noteDataSnapshot.child("ulasan").getValue(String.class).equalsIgnoreCase("-")) {
                                    buttonTulisReview.setVisibility(View.VISIBLE);
                                    linearLayoutUlasan.setVisibility(View.GONE);
                                } else {
                                    buttonTulisReview.setVisibility(View.GONE);
                                    linearLayoutUlasan.setVisibility(View.VISIBLE);
                                    ratingBarReview.setRating(Float.parseFloat(noteDataSnapshot.child("rating").getValue(String.class)));
                                    textViewUlasan.setText(noteDataSnapshot.child("ulasan").getValue(String.class));
                                }
                            } else {
                                buttonTulisReview.setVisibility(View.GONE);
                                linearLayoutUlasan.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference.child("Pelanggan").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        if (noteDataSnapshot.getKey().equals(userAppModel.getUserKey())) {
                            textViewNamaPelanggan.setText(noteDataSnapshot.child("nama").getValue(String.class));
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            buttonTulisReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ReviewActivity.class);
                    intent.putExtra("transaksiId", transaksiModel.getPelangganId());
                    v.getContext().startActivity(intent);
                }
            });
        } else {
            cardViewPelanggan.setVisibility(View.GONE);
            cardViewBengkel.setVisibility(View.VISIBLE);

            databaseReference.child("Pelanggan").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        if (transaksiModel.getPelangganId().equals(noteDataSnapshot.getKey())) {
                            textViewNamaPelangganBengkel.setText(noteDataSnapshot.child("nama").getValue(String.class));
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference.child("Review").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        if (transaksiModel.getBengkelId().equals(noteDataSnapshot.getKey())) {
                            textViewUlasanBengkel.setText(noteDataSnapshot.child("ulasan").getValue(String.class));
                            ratingBarBengkel.setRating(Float.parseFloat(noteDataSnapshot.child("rating").getValue(String.class)));
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            textViewLokasiKendaraanBengkel.setText(transaksiModel.getAlamat());
            textViewRincianBengkel.setText(transaksiModel.getRincian());
            textViewTanggalBengkel.setText(transaksiModel.getTanggal());
            textViewStatusBengkel.setText(transaksiModel.getStatus());
        }
    }
}