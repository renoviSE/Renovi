package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class RenterDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_details);
        String renterId = getIntent().getStringExtra("renterId");
        loadRenovierungenForRenter(renterId);
    }

    private void loadRenovierungenForRenter(String renterId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("renovierungen")
                .whereEqualTo("renterId", renterId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Handle each renovation
                            // For example, create views for each renovation
                        }
                    } else {
                        Log.d("FirebaseError", "Error getting documents: ", task.getException());
                    }
                });
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}