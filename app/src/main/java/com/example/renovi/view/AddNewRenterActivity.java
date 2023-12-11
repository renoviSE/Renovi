package com.example.renovi.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.renovi.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewRenterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_renter);
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        initializeAddRenterButton(db);

    }

    private void initializeAddRenterButton(FirebaseFirestore db) {
        Button addRenterButton = findViewById(R.id.addRenterButton);
        addRenterButton.setOnClickListener(v -> addRenter(db));
    }

    private void addRenter(FirebaseFirestore db) {
        EditText vornameEditText = findViewById(R.id.vornameField);
        EditText nachnameEditText = findViewById(R.id.nachnameField);
        EditText mieterIdEditText = findViewById(R.id.mieterIdField);

        // Extrahiert die Textwerte aus den Eingabefeldern
        String vorname = vornameEditText.getText().toString();
        String nachname = nachnameEditText.getText().toString();
        String mieter_id = mieterIdEditText.getText().toString();

        // Erstellt ein neues Datenobjekt
        Map<String, Object> benutzerDaten = new HashMap<>();
        benutzerDaten.put("Vorname", vorname);
        benutzerDaten.put("Nachname", nachname);
        benutzerDaten.put("Mieter ID", mieter_id);

        // Fügt das neue Dokument zur Benutzer-Sammlung hinzu
        db.collection("Mieter")
                .add(benutzerDaten)
                .addOnSuccessListener(documentReference -> {
                    // Erfolgreich hinzugefügt
                    Toast.makeText(this, "Benutzer erfolgreich hinzugefügt", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Fehler beim Hinzufügen
                    Toast.makeText(this, "Fehler beim Hinzufügen des Benutzers", Toast.LENGTH_SHORT).show();
                });
    }

    private void onAddRenterButtonClick() {
    }

}