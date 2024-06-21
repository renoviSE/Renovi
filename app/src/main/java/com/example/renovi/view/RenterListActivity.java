package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.renovi.R;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.ButtonCreator;
import com.example.renovi.viewmodel.Session;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class RenterListActivity extends AppCompatActivity {


    private ConstraintLayout mainLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_renter_list);
        mainLayout = findViewById(R.id.renterListConstraintLayout);


        initializeBackToMainButton();
        loadRenter();
    }


    private void loadRenter() {
        db.collection("Mieter")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String firstName = document.getString("vorname");
                            String lastName = document.getString("nachname");
                            String fullName = firstName + " " + lastName;
                            String renterId = document.getId();  // Annahme, dass die Dokument-ID die Mieter-ID ist
                            generateButtonForRenter(renterId, fullName);
                        }
                    } else {
                        generatePlaceholder();
                        Log.d("FirebaseError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void initializeBackToMainButton() {
        Button startButton = findViewById(R.id.InboxToMainButton);
        startButton.setOnClickListener(view -> switchToMain());
    }
    private void switchToMain() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
    }

    private void generateButtonForRenter(String renterId, String fullname) {
        ButtonCreator buttonCreator = new ButtonCreator(this);

        Button renterButton = buttonCreator.createButton(mainLayout, fullname, R.id.renterListConstraintLayout);
        renterButton.setOnClickListener(v -> switchToRenterDetails(renterId));
    }

    private void switchToRenterDetails(String renterId) {
        Intent intent = new Intent(this, RenterDetailsActivity.class);
        intent.putExtra("renterId", renterId);
        startActivity(intent);
    }

    private void generatePlaceholder() {
        // Erstellen und Hinzufügen des Platzhalers zum Layout
        ButtonCreator buttonCreator  = new ButtonCreator(this);
        TextView buttonsTitle = buttonCreator.createUpcomingSectionTitle(mainLayout, R.string.no_renter_found_string, R.id.renterListConstraintLayout);
        int topConstraint = buttonsTitle.getId();

        buttonCreator.createPlaceholderView(mainLayout, topConstraint);

    }
}