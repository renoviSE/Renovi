package com.example.renovi.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.renovi.R;
import com.example.renovi.model.MChatMessage;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.AnimationUtil;
import com.example.renovi.viewmodel.ButtonCreator;
import com.example.renovi.viewmodel.Session;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    final String TAG = "myTag";
    private ConstraintLayout mainLayout;
    ScrollView scrollView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;
    private String renterId;
    private EditText messageInput;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserFromSession();

        setContentView(R.layout.activity_chat);
        mainLayout = findViewById(R.id.chatConstraintLayout);
        scrollView = findViewById(R.id.scrollViewChats);
        messageInput = findViewById(R.id.newChatMessage);

        initializeBackToPreviousActivityButton();
        initializeSendPrivateMessageButton();

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
                                    document.getString("senderName"),
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
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void sendPrivateMessage() {
        String nachricht = messageInput.getText().toString().trim();

        // Farben und Animationen
        int dangerColor = ContextCompat.getColor(this, R.color.danger);
        int successColor = ContextCompat.getColor(this, R.color.lightBlue);
        int currentDrawableColor = ContextCompat.getColor(this, R.color.gray2);
        int animationDuration = 1000;

        // Validierung
        if (nachricht.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(messageInput, dangerColor, animationDuration);
            Toast.makeText(this, "Bitte Nachricht eingeben", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Objects.equals(user.getRole(), "Renter")) {
            // Wenn der Nutzer ein Mieter ist, muss erst der Vermieter ermittelt werden
            db.collection("Mieter")
                    .document(user.getId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String landlordID = documentSnapshot.getString("vermieter");
                            if (landlordID != null) {
                                sendMessageToChat(nachricht, landlordID);  // Nachricht senden
                            } else {
                                Toast.makeText(this, "Kein Vermieter gefunden", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Mieter-Dokument nicht gefunden", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Fehler beim Abrufen des Dokuments", e);
                        Toast.makeText(this, "Fehler beim Laden des Vermieters", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Wenn der Nutzer ein Vermieter ist
            sendMessageToChat(nachricht, renterId);  // Direkt senden
        }
    }

    private void sendMessageToChat(String nachricht, String recipientId) {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("from", user.getId());
        messageData.put("senderName", user.getFirstName() + " " + user.getLastName());
        messageData.put("to", recipientId);
        messageData.put("message", nachricht);
        messageData.put("timestamp", Timestamp.now());

        db.collection("Mieter")
                .document(renterId)  // Immer im Mieter-Dokument speichern
                .collection("Chat")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Nachricht an Mieter " + renterId + " gesendet.");

                    // Animation + UI reset
                    AnimationUtil.animateInputAndDrawableColor(messageInput, ContextCompat.getColor(this, R.color.gray2),
                            ContextCompat.getColor(this, R.color.lightBlue), 1000);

                    Toast.makeText(this, "Nachricht erfolgreich gesendet", Toast.LENGTH_SHORT).show();

                    new android.os.Handler().postDelayed(() -> {
                        messageInput.setText(""); // Feld leeren

                        // Erst nach kurzem Delay die View neu laden
                        recreate();
                        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                    }, 300);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Fehler beim Senden der Nachricht an " + renterId, e);
                    Toast.makeText(this, "Fehler beim Senden der Nachricht", Toast.LENGTH_SHORT).show();
                });
    }

    private void generateButtonForMessage(MChatMessage chat) {
        ButtonCreator bc = new ButtonCreator(this);
        boolean isSender = user.getId().equals(chat.getMessageFrom());

        // Title = entweder eigener Name oder otherPerson
        String title = isSender
                ? user.getFirstName() + " " + user.getLastName()
                : chat.getMessageSenderName();

        // Nachrichtentext
        String text  = chat.getMessage();

        // Timestamp formatieren
        String time  = new SimpleDateFormat("HH:mm  dd.MM.yy", Locale.getDefault())
                .format(chat.getTimestamp().toDate());

        bc.createChatBubble(
                mainLayout,
                isSender,
                title,
                text,
                time
        );
    }

    private void initializeBackToPreviousActivityButton() {
        Button goBackButton = findViewById(R.id.chatToPreviousButton);
        goBackButton.setOnClickListener(view -> switchToPreviousActivity());
    }
    private void initializeSendPrivateMessageButton() {
        Button sendPrivateMessageButton = findViewById(R.id.sendPrivateChatButton);
        sendPrivateMessageButton.setOnClickListener(view -> sendPrivateMessage());
    }


    private void switchToPreviousActivity() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
        overridePendingTransition(0, 0); // Deaktiviert Animation beim Zurückkehren
    }

    private void generatePlaceholder() {
        // Erstellen und Hinzufügen des Platzhalters zum Layout
        ButtonCreator buttonCreator = new ButtonCreator(this);

        buttonCreator.createPlaceholderView(mainLayout, R.id.renovationsListTopConstraintForPlaceholder, R.string.no_chat_placeholder_message);
    }
}
