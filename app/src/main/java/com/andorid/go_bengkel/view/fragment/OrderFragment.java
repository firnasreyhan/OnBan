package com.andorid.go_bengkel.view.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.adapter.RecyclerViewOrderAdapter;
import com.andorid.go_bengkel.adapter.RecyclerViewOrderBengkelAdapter;
import com.andorid.go_bengkel.model.DetailBengkelModel;
import com.andorid.go_bengkel.model.TransaksiModel;
import com.andorid.go_bengkel.model.UserAppModel;
import com.andorid.go_bengkel.preference.AppPreference;
import com.andorid.go_bengkel.view.activity.DetailBengkelActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OrderFragment extends Fragment {
    private final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private LinearLayout linearLayoutBengkelKosong, linearLayoutOrderKosong, llTarif;
    private RecyclerView recyclerViewOrder;
    private ProgressDialog progressDialog;

    private UserAppModel userAppModel;

    double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        userAppModel = AppPreference.getUser(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");

        recyclerViewOrder = view.findViewById(R.id.recyclerViewOrder);
        linearLayoutBengkelKosong = view.findViewById(R.id.linearLayoutBengkelKosong);
        linearLayoutOrderKosong = view.findViewById(R.id.linearLayoutOrderKosong);
        llTarif = view.findViewById(R.id.llTarif);
        progressDialog = new ProgressDialog(getContext());

        if (ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getLocation();
        }

        recyclerViewOrder.setVisibility(View.GONE);
        progressDialog.setMessage("Tunggu Sebentar...");
        progressDialog.show();
        if (userAppModel.getStatus().equalsIgnoreCase("pelanggan")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    llTarif.setVisibility(View.VISIBLE);
                    recyclerViewOrder.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                    databaseReference.child("Bengkel").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ArrayList<DetailBengkelModel> list = new ArrayList<>();
                            for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                Location loc1 = new Location("");
                                loc1.setLatitude(latitude);
                                loc1.setLongitude(longitude);

                                Location loc2 = new Location("");
                                loc2.setLatitude(Double.parseDouble(noteDataSnapshot.child("latitude").getValue(String.class)));
                                loc2.setLongitude(Double.parseDouble(noteDataSnapshot.child("longitude").getValue(String.class)));

                                double distanceInMeters = loc1.distanceTo(loc2)/1000;
                                Log.e("calculate", String.valueOf(distanceInMeters));

                                DetailBengkelModel model = new DetailBengkelModel(
                                        noteDataSnapshot.child("namaPemilikBengkel").getValue(String.class),
                                        noteDataSnapshot.child("namaBengkel").getValue(String.class),
                                        noteDataSnapshot.child("telepon").getValue(String.class),
                                        noteDataSnapshot.child("jamBuka").getValue(String.class),
                                        noteDataSnapshot.child("jamTutup").getValue(String.class),
                                        noteDataSnapshot.child("alamat").getValue(String.class),
                                        noteDataSnapshot.child("latitude").getValue(String.class),
                                        noteDataSnapshot.child("longitude").getValue(String.class),
                                        noteDataSnapshot.getKey(),
                                        distanceInMeters
                                );
                                list.add(model);
                            }
                            setRecyclerViewOrder(list);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }, 5000);
        } else {
            recyclerViewOrder.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
            llTarif.setVisibility(View.GONE);
            databaseReference.child("Transaksi").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<TransaksiModel> list = new ArrayList<>();
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        if (noteDataSnapshot.child("status").getValue(String.class).equalsIgnoreCase("Menunggu Konfirmasi")
                                && noteDataSnapshot.child("bengkelId").getValue(String.class).equalsIgnoreCase(userAppModel.getUserKey())) {
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
                    setRecyclerViewOrderBengkel(list);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setRecyclerViewOrderBengkel(ArrayList<TransaksiModel> list) {
        if (list.size() > 0) {
            recyclerViewOrder.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewOrder.setAdapter(new RecyclerViewOrderBengkelAdapter(list));
            recyclerViewOrder.setVisibility(View.VISIBLE);
            linearLayoutOrderKosong.setVisibility(View.GONE);
        } else {
            recyclerViewOrder.setVisibility(View.GONE);
            linearLayoutOrderKosong.setVisibility(View.VISIBLE);
        }
    }

    public void setRecyclerViewOrder(ArrayList<DetailBengkelModel> list) {
        if (list.size() > 0) {
            Collections.sort(list, new Comparator<DetailBengkelModel>() {
                @Override
                public int compare(DetailBengkelModel o1, DetailBengkelModel o2) {
                    return Double.compare(o1.getJarak(), o2.getJarak());
                }
            });
            recyclerViewOrder.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewOrder.setAdapter(new RecyclerViewOrderAdapter(list));
            recyclerViewOrder.setVisibility(View.VISIBLE);
            linearLayoutBengkelKosong.setVisibility(View.GONE);
        } else {
            recyclerViewOrder.setVisibility(View.GONE);
            linearLayoutBengkelKosong.setVisibility(View.VISIBLE);
        }
    }

    public void getLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            LocationServices.getFusedLocationProviderClient(getActivity())
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(getActivity())
                                    .removeLocationUpdates(this);
                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                int latestLocationIndex = locationResult.getLocations().size() - 1;
                                latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                                Log.e("latnet", String.valueOf(latitude));
                                Log.e("longnet", String.valueOf(longitude));
                            }
                        }
                    }, Looper.getMainLooper());
        }
    }
}