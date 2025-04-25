package com.example.renovi.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.UI.AnimationUtil;
import com.example.renovi.viewmodel.UI.ButtonCreator;
import com.example.renovi.viewmodel.UI.UIHelper;
import com.example.renovi.viewmodel.Session;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

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
    int dangerColor;
    int successColor;
    int animationDuration = 1000;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserFromSession();

        setContentView(R.layout.activity_chat);
        mainLayout = findViewById(R.id.chatConstraintLayout);
        scrollView = findViewById(R.id.scrollViewChats);
        messageInput = findViewById(R.id.newChatMessage);

        UIHelper.initializeBackButton(this, R.id.chatToPreviousButton);
        UIHelper.initializeViewFunction(this, R.id.sendPrivateChatButton, view -> sendPrivateMessage());

        // Farben und Animationen
        dangerColor = ContextCompat.getColor(this, R.color.danger);
        successColor = ContextCompat.getColor(this, R.color.lightBlue);

        extractRenterId();
    }

    private void extractRenterId() {
        // Empfangen des Intent, der diese Activity gestartet hat
        Intent intent = getIntent();

        // Extrahieren der übertragenen Variable 'renterId'
        if (intent != null && intent.hasExtra("renterId")) {
            renterId = intent.getStringExtra("renterId");
        } else {
            renterId = user.getId();
        }
    }

    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void sendPrivateMessage() {
        String nachricht = messageInput.getText().toString().trim();

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
                    messageInput.setText(""); // Feld leeren
                    AnimationUtil.animateHintAndDrawableColor(messageInput, successColor, animationDuration);

                    Toast.makeText(this, "Nachricht erfolgreich gesendet", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Fehler beim Senden der Nachricht an " + renterId, e);
                    Toast.makeText(this, "Fehler beim Senden der Nachricht", Toast.LENGTH_SHORT).show();
                });
    }

    private void generatePlaceholder() {
        // Erstellen und Hinzufügen des Platzhalters zum Layout
        ButtonCreator bubbleCreator = new ButtonCreator(this);

        bubbleCreator.createPlaceholderView(mainLayout, R.id.renovationsListTopConstraintForPlaceholder, R.string.no_chat_placeholder_message);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    private ListenerRegistration chatListener;

    @Override
    protected void onStart() {
        super.onStart();
        startChatListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (chatListener != null) chatListener.remove();
    }

    private void startChatListener() {

        chatListener = db.collection("Mieter")
                .document(renterId)
                .collection("Chat")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Chat‑Listener failed", e);
                        return;
                    }

                    // wenn keine Nachrichten da sind → Placeholder einfügen
                    if (snapshots != null && snapshots.isEmpty()) {
                        generatePlaceholder();
                        return;
                    }

                    // sonst alle vorhandenen Nachrichten laden und Bubbles anlegen
                    ButtonCreator bc = new ButtonCreator(this);
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            DocumentSnapshot doc = dc.getDocument();

                            // Daten aus Firestore ziehen
                            String text       = doc.getString("message");
                            String fromId     = doc.getString("from");
                            String senderName = doc.getString("senderName");
                            com.google.firebase.Timestamp ts = doc.getTimestamp("timestamp");

                            boolean isSender = user.getId().equals(fromId);
                            String title     = isSender
                                    ? user.getFirstName() + " " + user.getLastName()
                                    : senderName;

                            String time = new SimpleDateFormat(
                                    "HH:mm  dd.MM.yy",
                                    Locale.getDefault()
                            ).format(ts.toDate());

                            // Bubble anlegen
                            bc.createChatBubble(
                                    mainLayout,
                                    isSender,
                                    title,
                                    text,
                                    time
                            );
                        }
                    }
                    //Nach dem alle Bubbles geladen sind -> nach unten scrollen
                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                });
    }
}