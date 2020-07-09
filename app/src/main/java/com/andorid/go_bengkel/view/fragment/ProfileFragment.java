package com.andorid.go_bengkel.view.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.model.UserAppModel;
import com.andorid.go_bengkel.preference.AppPreference;
import com.andorid.go_bengkel.view.activity.EditBengkelActivity;
import com.andorid.go_bengkel.view.activity.EditPelangganActivity;
import com.andorid.go_bengkel.view.activity.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private Button buttonKeluar, buttonPerbaruiProfil;
    private CardView cardViewBengkel, cardViewPelanggan;
    private TextView textViewNamaPelanggan, textViewEmailPelanggan, textViewNomorTeleponPelanggan;
    private TextView textViewNamaPemilikBengkel, textViewNamaBengkel, textViewNomorTeleponBengkel, textViewEmailBengkel, textViewjamOperasional, textViewAlamat;

    private UserAppModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        model = AppPreference.getUser(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");

        cardViewPelanggan = view.findViewById(R.id.cardViewPelanggan);
        textViewEmailPelanggan = view.findViewById(R.id.textViewEmailPelanggan);
        textViewNamaPelanggan = view.findViewById(R.id.textViewNamaPelanggan);
        textViewNomorTeleponPelanggan = view.findViewById(R.id.textViewNomorTeleponPelanggan);

        cardViewBengkel = view.findViewById(R.id.cardViewBengkel);
        textViewNamaPemilikBengkel = view.findViewById(R.id.textViewNamaPemilikBengkel);
        textViewNamaBengkel = view.findViewById(R.id.textViewNamaBengkel);
        textViewEmailBengkel = view.findViewById(R.id.textViewEmailBengkel);
        textViewNomorTeleponBengkel = view.findViewById(R.id.textViewNomorTeleponBengkel);
        textViewjamOperasional = view.findViewById(R.id.textViewJamOperasional);
        textViewAlamat = view.findViewById(R.id.textViewAlamat);

        buttonKeluar = view.findViewById(R.id.buttonKeluar);
        buttonPerbaruiProfil = view.findViewById(R.id.buttonPerbaruiProfil);

        if (model.getStatus().equalsIgnoreCase("Pelanggan")) {
            cardViewBengkel.setVisibility(View.GONE);
            cardViewPelanggan.setVisibility(View.VISIBLE);
        } else {
            cardViewBengkel.setVisibility(View.VISIBLE);
            cardViewPelanggan.setVisibility(View.GONE);
        }

        getAccountFirebase();

        buttonPerbaruiProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppPreference.getUser(v.getContext()).getStatus().equalsIgnoreCase("Pelanggan")) {
                    startActivity(new Intent(v.getContext(), EditPelangganActivity.class));
                } else {
                    startActivity(new Intent(v.getContext(), EditBengkelActivity.class));
                }
            }
        });

        buttonKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Apakah anda yakin ingin keluar dari aplikasi?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                FirebaseDatabase.getInstance().getReference("OnBan").child("Token").child(model.getUserKey()).removeValue();
                                AppPreference.removeUser(getContext());
                                Toast.makeText(getActivity(), "Anda berhasil keluar dari aplikasi", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        return view;
    }

    public void getAccountFirebase() {
        if (model.getStatus().equalsIgnoreCase("Pelanggan")) {
            databaseReference.child("Pelanggan").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        String userKey = noteDataSnapshot.getKey();
                        if (userKey.equals(model.getUserKey())) {
                            textViewNamaPelanggan.setText(noteDataSnapshot.child("nama").getValue(String.class));
                            textViewNomorTeleponPelanggan.setText(noteDataSnapshot.child("telepon").getValue(String.class));
                            textViewEmailPelanggan.setText(model.getEmail());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (model.getStatus().equalsIgnoreCase("Bengkel")) {
            databaseReference.child("Bengkel").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        String userKey = noteDataSnapshot.getKey();
                        if (userKey.equals(model.getUserKey())) {
                            if (userKey.equals(model.getUserKey())) {
                                textViewNamaPemilikBengkel.setText(noteDataSnapshot.child("namaPemilikBengkel").getValue(String.class));
                                textViewNamaBengkel.setText(noteDataSnapshot.child("namaBengkel").getValue(String.class));
                                textViewNomorTeleponBengkel.setText(noteDataSnapshot.child("telepon").getValue(String.class));
                                textViewjamOperasional.setText(noteDataSnapshot.child("jamBuka").getValue(String.class) + " - " + noteDataSnapshot.child("jamTutup").getValue(String.class));
                                textViewAlamat.setText(noteDataSnapshot.child("alamat").getValue(String.class));
                                textViewEmailBengkel.setText(model.getEmail());
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
}