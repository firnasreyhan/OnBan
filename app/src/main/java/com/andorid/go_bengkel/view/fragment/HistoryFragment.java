package com.andorid.go_bengkel.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.adapter.RecyclerViewHistoryAdapter;
import com.andorid.go_bengkel.adapter.RecyclerViewReviewAdapter;
import com.andorid.go_bengkel.model.DetailBengkelModel;
import com.andorid.go_bengkel.model.TransaksiModel;
import com.andorid.go_bengkel.model.UserAppModel;
import com.andorid.go_bengkel.preference.AppPreference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private RecyclerView recyclerViewHistory;
    private LinearLayout linearLayoutRiwayatKosong;

    private UserAppModel userAppModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        userAppModel = AppPreference.getUser(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");

        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);
        linearLayoutRiwayatKosong = view.findViewById(R.id.linearLayoutRiwayatKosong);

        databaseReference.child("Transaksi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<TransaksiModel> list = new ArrayList<>();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    if (userAppModel.getStatus().equalsIgnoreCase("Pelanggan")) {
                        if (userAppModel.getUserKey().equals(noteDataSnapshot.child("pelangganId").getValue(String.class))) {
                            TransaksiModel model = new TransaksiModel(
                                    noteDataSnapshot.getKey(),
                                    noteDataSnapshot.child("bengkelId").getValue(String.class),
                                    noteDataSnapshot.child("latitude").getValue(String.class),
                                    noteDataSnapshot.child("longitude").getValue(String.class),
                                    noteDataSnapshot.child("alamat").getValue(String.class),
                                    noteDataSnapshot.child("rincian").getValue(String.class),
                                    noteDataSnapshot.child("tanggal").getValue(String.class),
                                    noteDataSnapshot.child("status").getValue(String.class)
                            );
                            list.add(model);
                        }
                    } else {
                        if (userAppModel.getUserKey().equals(noteDataSnapshot.child("bengkelId").getValue(String.class))
                                && !noteDataSnapshot.child("status").getValue(String.class).equalsIgnoreCase("Menunggu Konfirmasi")) {
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
                }
                setRecyclerViewHistory(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    public void setRecyclerViewHistory(ArrayList<TransaksiModel> list) {
        if (list.size() > 0) {
            recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewHistory.setAdapter(new RecyclerViewHistoryAdapter(list, userAppModel.getStatus(), userAppModel.getUserKey()));
            linearLayoutRiwayatKosong.setVisibility(View.GONE);
            recyclerViewHistory.setVisibility(View.VISIBLE);
        } else {
            linearLayoutRiwayatKosong.setVisibility(View.VISIBLE);
            recyclerViewHistory.setVisibility(View.GONE);
        }
    }
}