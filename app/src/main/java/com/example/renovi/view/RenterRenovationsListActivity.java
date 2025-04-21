package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.model.Renovation;
import com.example.renovi.viewmodel.ButtonCreator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        initializeBackToPreviousActivityButton();

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
        // Greife auf die Sammlung "Renovationen" des aktuellen Mieters zu
        db.collection("Mieter").document(renterId).collection("Renovierungen")
                .get()
                .addOnSuccessListener(documents -> {
                    BigDecimal allObjectsValue = new BigDecimal("0");

                    // Daten erfolgreich erhalten
                    int buttonId = 1;
                    for (DocumentSnapshot document : documents.getDocuments()) {
                        if (document.exists()) {
                            String objectName = document.getString("object");
                            Renovation renovation = new Renovation(
                                    document.getString("object"),
                                    document.getString("vorteile"),
                                    document.getString("nachteile"),
                                    document.getString("kosten"),
                                    document.getString("paragraph"),
                                    document.getString("zustand")
                            );

                            // Erstelle einen Button für jede Renovierung
                            generateButtonForRenter(objectName, renovation);

                            // Summiere die Kosten aller Objekte
                            allObjectsValue = allObjectsValue.add(renovation.getObjectValue());

                            buttonId += 1;
                            Log.i(TAG, "Renovierung erfolgreich geladen.");
                        }
                    }
                    // Wenn keine Renovierungen vorhanden sind
                    if (buttonId == 1) {
                        generatePlaceholder();
                    }

                })
                .addOnFailureListener(e -> {
                    // Fehler beim Abrufen der Daten
                    Log.e(TAG, "Fehler beim Abrufen der Renovierungen", e);
                });
    }

    private void initializeBackToPreviousActivityButton() {
        Button startButton = findViewById(R.id.chatToPreviousButton);
        startButton.setOnClickListener(view -> switchToPreviousActivity());
    }

    private void switchToPreviousActivity() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
        overridePendingTransition(0, 0); // Deaktiviert Animation beim Zurückkehren
    }

    private void generateButtonForRenter(String renovationTitle, Renovation renovation) {
        // Erstellen und Hinzufügen des Buttons zum Layout
        if (renovationTitle != null) {
            ButtonCreator buttonCreator = new ButtonCreator(this);

            Button renoButton = buttonCreator.createButton(mainLayout, renovationTitle, R.id.renovationsListScrollSpacer);
            renoButton.setOnClickListener(view -> switchToDetails(renovation));
        }
    }

    private void switchToDetails(Renovation renovation) {
        Intent switchActivityIntent = new Intent(this, DetailsActivity.class);
        switchActivityIntent.putExtra("renovierung", renovation);
        startActivity(switchActivityIntent);
    }

    private void generatePlaceholder() {
        // Erstellen und Hinzufügen des Platzhalters zum Layout
        ButtonCreator buttonCreator = new ButtonCreator(this);

        buttonCreator.createPlaceholderView(mainLayout, R.id.renovationsListTopConstraintForPlaceholder, R.string.no_renovations_placeholder_message);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}