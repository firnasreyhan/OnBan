package com.andorid.go_bengkel.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.adapter.RecyclerViewOrderAdapter;
import com.andorid.go_bengkel.adapter.RecyclerViewReviewAdapter;
import com.andorid.go_bengkel.model.DetailBengkelModel;
import com.andorid.go_bengkel.model.TransaksiModel;
import com.andorid.go_bengkel.model.UserAppModel;
import com.andorid.go_bengkel.model.UserModel;
import com.andorid.go_bengkel.preference.AppPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailBengkelActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;

    private DetailBengkelModel detailBengkelModel;
    private UserAppModel userAppModel;

    private Button buttonPesanLayanan;
    private TextView textViewNamaPemilikBengkel, textViewNamaBengkel, textViewNomorTeleponBengkel, textViewEmailBengkel, textViewjamOperasional, textViewAlamat;
    private RecyclerView recyclerViewUlasan;

    private String transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_bengkel);
        setTitle("Detail Bengkel");

        userAppModel = AppPreference.getUser(this);
        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");
        transactionId = databaseReference.push().getKey();

        detailBengkelModel = (DetailBengkelModel) getIntent().getSerializableExtra("bengkel");

        textViewNamaPemilikBengkel = findViewById(R.id.textViewNamaPemilikBengkel);
        textViewNamaBengkel = findViewById(R.id.textViewNamaBengkel);
        textViewEmailBengkel = findViewById(R.id.textViewEmailBengkel);
        textViewNomorTeleponBengkel = findViewById(R.id.textViewNomorTeleponBengkel);
        textViewjamOperasional = findViewById(R.id.textViewJamOperasional);
        textViewAlamat = findViewById(R.id.textViewAlamat);
        buttonPesanLayanan = findViewById(R.id.buttonPesanLayanan);
        recyclerViewUlasan = findViewById(R.id.recyclerViewUlasan);

        textViewNamaPemilikBengkel.setText(detailBengkelModel.getNamaPemilikBengkel());
        textViewNamaBengkel.setText(detailBengkelModel.getNamaBengkel());
        textViewNomorTeleponBengkel.setText(detailBengkelModel.getTelepon());
        textViewjamOperasional.setText(detailBengkelModel.getJamBuka() + " - " + detailBengkelModel.getJamTutup());
        textViewAlamat.setText(detailBengkelModel.getAlamat());

        getTransaction();

        buttonPesanLayanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailBengkelActivity.this, OrderActivity.class);
                intent.putExtra("bengkelId", detailBengkelModel.getUserKey());
                startActivity(intent);
            }
        });
    }

    public void getTransaction() {
        databaseReference.child("Transaksi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<TransaksiModel> list = new ArrayList<>();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    if (noteDataSnapshot.child("bengkelId").getValue(String.class).equals(detailBengkelModel.getUserKey())
                            && noteDataSnapshot.child("status").getValue(String.class).equalsIgnoreCase("Selesai")) {
                        TransaksiModel model = new TransaksiModel(
                                noteDataSnapshot.child("pelangganId").getValue(String.class),
                                noteDataSnapshot.getKey(),
                                noteDataSnapshot.child("latitude").getValue(String.class),
                                noteDataSnapshot.child("longitude").getValue(String.class),
                                noteDataSnapshot.child("alamat").getValue(String.class),
                                noteDataSnapshot.child("rincian").getValue(String.class),
                                noteDataSnapshot.child("tanggal").getValue(String.class),
                                noteDataSnapshot.child("status").getValue(String.class)
                        );
                        list.add(model);
                    }
                }
                setRecyclerViewUlasan(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setRecyclerViewUlasan(ArrayList<TransaksiModel> list) {
        recyclerViewUlasan.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUlasan.setAdapter(new RecyclerViewReviewAdapter(list));
    }
}