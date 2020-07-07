package com.andorid.go_bengkel.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.andorid.go_bengkel.R;
import com.andorid.go_bengkel.model.ReviewModel;
import com.andorid.go_bengkel.model.UserAppModel;
import com.andorid.go_bengkel.preference.AppPreference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewActivity extends Activity {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private RatingBar ratingBarReview;
    private EditText editTextReview;
    private Button buttonReview;

    private UserAppModel userAppModel;
    private String transaksiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        userAppModel = AppPreference.getUser(this);
        transaksiId = getIntent().getStringExtra("transaksiId");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("OnBan");

        ratingBarReview = findViewById(R.id.ratingBarReview);
        editTextReview = findViewById(R.id.editTextReview);
        buttonReview = findViewById(R.id.buttonReview);

        buttonReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReview(
                        transaksiId,
                        String.valueOf(ratingBarReview.getRating()),
                        editTextReview.getText().toString()
                );
                finish();
            }
        });
    }

    public void addReview(String transaksiId, String rating, String ulasan) {
        databaseReference.child("Review").child(transaksiId).child("rating").setValue(rating);
        databaseReference.child("Review").child(transaksiId).child("ulasan").setValue(ulasan);
    }
}