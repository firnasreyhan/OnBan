package com.andorid.go_bengkel.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

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

public class RecyclerViewReviewAdapter extends RecyclerView.Adapter<RecyclerViewReviewAdapter.ViewHolder> {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private ArrayList<TransaksiModel> list;

    public RecyclerViewReviewAdapter(ArrayList<TransaksiModel> list) {
        this.list = list;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewReviewAdapter.ViewHolder holder, int position) {
        TransaksiModel model = list.get(position);

        databaseReference.child("Review").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    if (noteDataSnapshot.getKey().equals(model.getBengkelId())) {
                        holder.ratingBarTransaksi.setRating(Float.parseFloat(noteDataSnapshot.child("rating").getValue(String.class)));
                        holder.textViewUlasan.setText(noteDataSnapshot.child("ulasan").getValue(String.class));
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
                    if (noteDataSnapshot.getKey().equals(model.getPelangganId())) {
                        holder.textViewNamaPelanggan.setText(noteDataSnapshot.child("nama").getValue(String.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewNamaPelanggan, textViewUlasan;
        private RatingBar ratingBarTransaksi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNamaPelanggan = itemView.findViewById(R.id.textViewNamaPelanggan);
            textViewUlasan = itemView.findViewById(R.id.textViewUlasan);
            ratingBarTransaksi = itemView.findViewById(R.id.ratingBarTransaksi);
        }
    }
}
