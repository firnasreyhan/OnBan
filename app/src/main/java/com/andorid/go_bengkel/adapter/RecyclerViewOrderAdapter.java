package com.andorid.go_bengkel.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.model.BengkelModel;
import com.andorid.go_bengkel.model.DetailBengkelModel;
import com.andorid.go_bengkel.view.activity.DetailBengkelActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecyclerViewOrderAdapter extends RecyclerView.Adapter<RecyclerViewOrderAdapter.ViewHolder> {
    private ArrayList<DetailBengkelModel> list;

    public RecyclerViewOrderAdapter(ArrayList<DetailBengkelModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewOrderAdapter.ViewHolder holder, int position) {
        DetailBengkelModel model = list.get(position);

        holder.textViewNamaBengkel.setText(model.getNamaBengkel());
        holder.textViewJarakLokasi.setText(new DecimalFormat("##.##").format(model.getJarak()) + " km");
        holder.textViewAlamatBengkel.setText(model.getAlamat());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailBengkelActivity.class);
                intent.putExtra("bengkel", model);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewNamaBengkel, textViewAlamatBengkel, textViewJarakLokasi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNamaBengkel = itemView.findViewById(R.id.textViewNamaBengkel);
            textViewAlamatBengkel = itemView.findViewById(R.id.textViewAlamatBengkel);
            textViewJarakLokasi = itemView.findViewById(R.id.textViewJarak);
        }
    }
}
