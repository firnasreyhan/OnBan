package com.andorid.go_bengkel.adapter;

import android.app.AlertDialog;
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
import com.andorid.go_bengkel.view.activity.DetailBengkelActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerViewOrderBengkelAdapter extends RecyclerView.Adapter<RecyclerViewOrderBengkelAdapter.ViewHolder> {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private ArrayList<TransaksiModel> list;

    public RecyclerViewOrderBengkelAdapter(ArrayList<TransaksiModel> list) {
        this.list = list;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_bengkel, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewOrderBengkelAdapter.ViewHolder holder, int position) {
        TransaksiModel model = list.get(position);

        databaseReference.child("Pelanggan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    if (noteDataSnapshot.getKey().equals(model.getPelangganId())) {
                        holder.textViewNamaPelanggan.setText(noteDataSnapshot.child("nama").getValue(String.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.textViewAlamat.setText(model.getAlamat());
        holder.textViewRincian.setText(model.getRincian());

        holder.buttonTerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menerima transaksi ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(v.getContext(), "Transaksi telah diterima, mohon segera untuk diproses", Toast.LENGTH_SHORT).show();
                                databaseReference.child("Transaksi").child(model.getBengkelId()).child("status").setValue("Sedang Diproses");
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

        holder.buttonTolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah anda yakin ingin menolak transaksi ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(v.getContext(), "Transaksi berhasil ditolak", Toast.LENGTH_SHORT).show();
                                databaseReference.child("Transaksi").child(model.getBengkelId()).removeValue();
                                databaseReference.child("Review").child(model.getBengkelId()).removeValue();
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
        private TextView textViewNamaPelanggan, textViewAlamat, textViewRincian;
        private Button buttonTerima, buttonTolak;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNamaPelanggan = itemView.findViewById(R.id.textViewNamaPelanggan);
            textViewAlamat = itemView.findViewById(R.id.textViewAlamat);
            textViewRincian = itemView.findViewById(R.id.textViewRincian);
            buttonTerima = itemView.findViewById(R.id.buttonTerima);
            buttonTolak = itemView.findViewById(R.id.buttonTolak);
        }
    }
}
