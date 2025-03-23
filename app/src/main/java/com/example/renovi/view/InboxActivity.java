package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.renovi.R;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.ButtonCreator;
import com.example.renovi.viewmodel.Session;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class InboxActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserFromSession();

        setContentView(R.layout.activity_inbox);
        mainLayout = findViewById(R.id.renterListConstraintLayout);

        initializeBackToPreviousActivityButton();
        loadRenter();
    }
    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }
    private void initializeBackToPreviousActivityButton() {
        Button returnButton = findViewById(R.id.InboxToMainButton);
        returnButton.setOnClickListener(view -> switchToPreviousActivity());
    }
    private void switchToPreviousActivity() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
    }

    //'--------------------------------------------------------------------------------------'
    private void loadRenter() {
        db.collection("Mieter")
                .whereEqualTo("vermieter", user.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(task.getResult().isEmpty()) {
                            generatePlaceholder();
                        } else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String firstName = document.getString("vorname");
                                String lastName = document.getString("nachname");
                                String fullName = firstName + " " + lastName;
                                String renterId = document.getId();
                                generateButtonForRenter(renterId, fullName);
                            }
                        }
                    } else {
                        generatePlaceholder();
                        Log.d("FirebaseError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void generateButtonForRenter(String renterId, String fullname) {
        ButtonCreator buttonCreator = new ButtonCreator(this);

        Button renterButton = buttonCreator.createButton(mainLayout, fullname, R.id.renterListScrollSpacer);
        renterButton.setOnClickListener(v -> switchToRenterRenovationList(renterId));
    }

    private void switchToRenterRenovationList(String renterId) {
        Intent intent = new Intent(this, RenterRenovationsListActivity.class);
        intent.putExtra("renterId", renterId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0,0); //disables animation
        startActivity(intent);
    }

    private void generatePlaceholder() {
        // Erstellen und Hinzufügen des Platzhalers zum Layout
        ButtonCreator buttonCreator  = new ButtonCreator(this);
        buttonCreator.createPlaceholderView(mainLayout, R.id.renterListTopConstraintForPlaceholder, R.string.no_renter_placeholder_message);

    }
}