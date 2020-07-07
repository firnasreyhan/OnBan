package com.andorid.go_bengkel.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.model.DetailBengkelModel;
import com.andorid.go_bengkel.model.TransaksiModel;
import com.andorid.go_bengkel.model.UserAppModel;
import com.andorid.go_bengkel.preference.AppPreference;
import com.andorid.go_bengkel.view.activity.DetailBengkelActivity;
import com.andorid.go_bengkel.view.activity.DetailHistoryActivity;
import com.andorid.go_bengkel.view.activity.ReviewActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerViewHistoryAdapter extends RecyclerView.Adapter<RecyclerViewHistoryAdapter.ViewHolder> {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private ArrayList<TransaksiModel> list;

    private String status, userKey;

    public RecyclerViewHistoryAdapter(ArrayList<TransaksiModel> list, String status, String userKey) {
        this.list = list;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");
        this.status = status;
        this.userKey = userKey;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHistoryAdapter.ViewHolder holder, int position) {
        TransaksiModel model = list.get(position);

        if (status.equalsIgnoreCase("Pelanggan")) {
            databaseReference.child("Bengkel").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        if (model.getBengkelId().equals(noteDataSnapshot.getKey())) {
                            holder.textViewNamaBengkel.setText(noteDataSnapshot.child("namaBengkel").getValue(String.class));
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
                        if (noteDataSnapshot.getKey().equals(model.getPelangganId())) {
                            if (noteDataSnapshot.child("ulasan").getValue(String.class).equalsIgnoreCase("-")
                                    && model.getStatus().equalsIgnoreCase("Selesai")) {
                                holder.buttonTulisReview.setVisibility(View.VISIBLE);
                            } else {
                                holder.buttonTulisReview.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            if (model.getStatus().equalsIgnoreCase("Sedang Diproses")) {
                holder.buttonSelesaikanTransaksi.setVisibility(View.VISIBLE);
            }

            databaseReference.child("Pelanggan").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        if (noteDataSnapshot.getKey().equals(model.getPelangganId())) {
                            holder.textViewNamaBengkel.setText(noteDataSnapshot.child("nama").getValue(String.class));
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        holder.textViewStatusTransaksi.setText(model.getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailHistoryActivity.class);
                intent.putExtra("transaksi", model);
                v.getContext().startActivity(intent);
            }
        });

        holder.buttonTulisReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReviewActivity.class);
                intent.putExtra("transaksiId", model.getPelangganId());
                v.getContext().startActivity(intent);
            }
        });

        holder.buttonSelesaikanTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menyelesaikan transaksi ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(v.getContext(), "Transaksi telah berhasil diselesaikan", Toast.LENGTH_SHORT).show();
                                databaseReference.child("Transaksi").child(model.getBengkelId()).child("status").setValue("Selesai");
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
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewNamaBengkel, textViewStatusTransaksi;
        private Button buttonTulisReview, buttonSelesaikanTransaksi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNamaBengkel = itemView.findViewById(R.id.textViewNamaBengkel);
            textViewStatusTransaksi = itemView.findViewById(R.id.textViewStatusTransaksi);
            buttonTulisReview = itemView.findViewById(R.id.buttonTulisReview);
            buttonSelesaikanTransaksi = itemView.findViewById(R.id.buttonSelesaikanTransaksi);
        }
    }
}
