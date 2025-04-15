package com.example.renovi.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.renovi.R;
import com.example.renovi.model.MChatMessage;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.ButtonCreator;
import com.example.renovi.viewmodel.Session;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.math.BigDecimal;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    final String TAG = "myTag";
    private ConstraintLayout mainLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;
    private String renterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserFromSession();

        setContentView(R.layout.activity_chat);
        mainLayout = findViewById(R.id.renterListConstraintLayout);

        initializeBackToPreviousActivityButton();

        // Empfangen des Intent, der diese Activity gestartet hat
        Intent intent = getIntent();

        // Extrahieren der übertragenen Variable 'renterId'
        if (intent != null && intent.hasExtra("renterId")) {
            renterId = intent.getStringExtra("renterId");
            getChats(db, renterId, user.getId());
        } else {
            renterId = user.getId();
            db.collection("Mieter")
                    .document(user.getId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String landlordID = documentSnapshot.getString("vermieter");
                            getChats(db, renterId, landlordID);

                        } else {
                            Log.d("Firestore", "Dokument nicht gefunden.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Fehler beim Abrufen des Dokuments", e);
                    });
        }
    }

    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void getChats(FirebaseFirestore db, String renterId, String landlordId) {
        db.collection("Mieter").document(renterId).collection("Chat").orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(documents -> {
                    BigDecimal allObjectsValue = new BigDecimal("0");

                    // Daten erfolgreich erhalten
                    int buttonId = 1;
                    for (DocumentSnapshot document : documents.getDocuments()) {
                        if (document.exists()) {
                            //String objectName = document.getString("object");
                            MChatMessage chat = new MChatMessage(
                                    document.getString("message"),
                                    document.getString("from"),
                                    document.getString("to"),
                                    document.getTimestamp("timestamp")

                            );

                            // Erstelle einen Button für jede Renovierung
                            if((Objects.equals(chat.getMessageFrom(), landlordId) || Objects.equals(chat.getMessageTo(), landlordId)) && (Objects.equals(chat.getMessageFrom(), renterId) || Objects.equals(chat.getMessageTo(), renterId)) ){
                                generateButtonForMessage(chat);
                            }

                            buttonId += 1;
                            Log.i(TAG, "Renovierung erfolgreich geladen.");
                        }
                    }
                    // Wenn kein chat vorhanden sind
                    if (buttonId == 1) {
                        generatePlaceholder();
                    }

                })
                .addOnFailureListener(e -> {
                    // Fehler beim Abrufen der Daten
                    Log.e(TAG, "Fehler beim Abrufen der Renovierungen", e);
                });
    }

    private void generateButtonForMessage(MChatMessage chat) {
        // Erstellen und Hinzufügen des Buttons zum Layout
        if (chat != null) {
            ButtonCreator buttonCreator = new ButtonCreator(this);
            String message = chat.getMessage();
            if (Objects.equals(user.getId(), chat.getMessageFrom())) {
                Button renoButton = buttonCreator.createColoredButton(mainLayout, message, R.color.midBlue, R.id.renovationsListScrollSpacer);
            }
            else if (Objects.equals(user.getId(), chat.getMessageTo())){
                Button renoButton = buttonCreator.createColoredButton(mainLayout, message, R.color.lightPurple, R.id.renovationsListScrollSpacer);
            }
        }
    }

    private void initializeBackToPreviousActivityButton() {
        Button startButton = findViewById(R.id.InboxToMainButton);
        startButton.setOnClickListener(view -> switchToPreviousActivity());
    }

    private void switchToPreviousActivity() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
        overridePendingTransition(0, 0); // Deaktiviert Animation beim Zurückkehren
    }

    private void generatePlaceholder() {
        // Erstellen und Hinzufügen des Platzhalters zum Layout
        ButtonCreator buttonCreator = new ButtonCreator(this);

        buttonCreator.createPlaceholderView(mainLayout, R.id.renovationsListTopConstraintForPlaceholder, R.string.no_renovations_placeholder_message);
    }
}
