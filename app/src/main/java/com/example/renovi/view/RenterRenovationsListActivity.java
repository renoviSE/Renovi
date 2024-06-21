package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.model.Renovation;
import com.example.renovi.model.Renter;
import com.example.renovi.viewmodel.ButtonCreator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.math.BigDecimal;

public class RenterRenovationsListActivity extends AppCompatActivity {

    final String TAG = "myTag";

    private ConstraintLayout mainLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_renter_renovations);
        mainLayout = findViewById(R.id.renovationsListConstraintLayout);

        initializeBackToMainButton();

        // Empfangen des Intent, der diese Activity gestartet hat
        Intent intent = getIntent();

        // Extrahieren der übertragenen Variable 'renterId'
        if (intent != null && intent.hasExtra("renterId")) {
            String renterId = intent.getStringExtra("renterId");

            getRenovierungen(db, renterId);

        } else {
            Toast.makeText(this, "Keine Mieter-ID übertragen!", Toast.LENGTH_LONG).show();
        }
    }


    private void getRenovierungen(FirebaseFirestore db, String renterId) {
        db.collection("Renovierung")
                .whereEqualTo("mieter", db.collection("mieter").document(renterId))
                .get()
                .addOnSuccessListener(documents -> {
                    BigDecimal allObjectsValue = new BigDecimal("0");

                    // Daten erfolgreich erhalten
                    int buttonId = 1;
                    for (DocumentSnapshot document : documents.getDocuments()) {
                        if (document.exists()) {
                            String objectName = document.getString("object");
                            Renovation renovation = new Renovation(document.getString("object"), document.getString("vorteile"), document.getString("nachteile"), document.getString("kosten"), document.getString("paragraph"), document.getString("zustand"));
                            // Erstelle einen Button für jeden Mieter
                            generateButtonForRenter(objectName, renovation);

                            // Speichere Kosten von allen Objekten
                            allObjectsValue = allObjectsValue.add(renovation.getObjectValue());

                            buttonId+=1;
                            Log.i(TAG, "Good Job");
                        }
                    }
                    // Wenn keine Renovierungen vorhanden sind
                    if (buttonId == 1) {
                        generatePlaceholder();
                    }

                })
                .addOnFailureListener(e -> {
                    // Fehler beim Abrufen der Daten
                    Log.i(TAG, "NO");

                });
    }

    private void initializeBackToMainButton() {
        Button startButton = findViewById(R.id.InboxToMainButton);
        startButton.setOnClickListener(view -> switchToMain());
    }
    private void switchToMain() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
    }

    private void generateButtonForRenter(String renovationTitle, Renovation renovation) {

        // Erstellen und Hinzufügen des Buttons zum Layout
        if (renovationTitle != null) {
            ButtonCreator buttonCreator  = new ButtonCreator(this);

            Button renoButton = buttonCreator.createButton(mainLayout, renovationTitle, R.id.renovationsListConstraintLayout, R.id.renovationsListScrollSpacer);
            renoButton.setOnClickListener(view -> switchToDetails(renovation)); //hier renoId eigentlich
        }
    }

    private void switchToDetails(Renovation renovation) {
        Intent switchActivityIntent = new Intent(this, DetailsActivity.class);
        switchActivityIntent.putExtra("renovierung", renovation);
        startActivity(switchActivityIntent);
    }

    private void generatePlaceholder() {
        // Erstellen und Hinzufügen des Platzhalers zum Layout
        ButtonCreator buttonCreator  = new ButtonCreator(this);

        buttonCreator.createPlaceholderView(mainLayout, R.id.renovationsListConstraintLayout);

    }
}