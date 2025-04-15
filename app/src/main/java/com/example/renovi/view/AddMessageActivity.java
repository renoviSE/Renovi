package com.example.renovi.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.renovi.R;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.AnimationUtil;
import com.example.renovi.viewmodel.MultiSelectDialogUtil;
import com.example.renovi.viewmodel.Session;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddMessageActivity extends AppCompatActivity {

    private EditText messageInput;
    private TextView recipientTextView;
    private Button sendMessageButton;

    private String[] renterArray;
    private boolean[] selectedRenter;
    private ArrayList<Integer> renterList = new ArrayList<>();
    private ArrayList<String> renterDocIds = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        getUserFromSession();

        messageInput = findViewById(R.id.messageContentInput);
        recipientTextView = findViewById(R.id.messageRecipient);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        initializeBackToPreviousActivityButton();

        loadRenter();

        recipientTextView.setOnClickListener(v -> {
            if (renterArray != null && renterArray.length > 0) {
                selectedRenter = new boolean[renterArray.length];
                for (int i = 0; i < renterArray.length; i++) {
                    if (renterList.contains(i)) {
                        selectedRenter[i] = true;
                    }
                }

                MultiSelectDialogUtil.showMultiSelectDialog(this, getString(R.string.selection_view_renter_title),
                        renterArray, selectedRenter, renterList, recipientTextView);
            }
        });

        sendMessageButton.setOnClickListener(v -> sendMessageToDatabase());
    }

    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void loadRenter() {
        db.collection("Mieter")
                .whereEqualTo("vermieter", user.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> renters = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String fullName = doc.getString("vorname") + " " + doc.getString("nachname");
                            renters.add(fullName);
                            renterDocIds.add(doc.getId());
                        }
                        renterArray = renters.toArray(new String[0]);
                    } else {
                        Log.w("Firestore", "Fehler beim Laden der Mieter", task.getException());
                    }
                });
    }

    private void sendMessageToDatabase() {
        String nachricht = messageInput.getText().toString().trim();

        int dangerColor = ContextCompat.getColor(this, R.color.danger);
        int successColor = ContextCompat.getColor(this, R.color.lightBlue);
        int currentDrawableColor = ContextCompat.getColor(this, R.color.gray2);
        int animationDuration = 1000;

        boolean isValid = true;
        if (nachricht.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(messageInput, dangerColor, animationDuration);
            isValid = false;
        }
        if (renterList.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(recipientTextView, dangerColor, animationDuration);
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Bitte Nachricht eingeben und mindestens einen Empfänger auswählen", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int index : renterList) {
            String renterDocId = renterDocIds.get(index);

            // Nachricht mit Zeitstempel
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("from", user.getId()); // Absender (Vermieter)
            messageData.put("to", renterDocId); // Empfänger (Mieter)
            messageData.put("message", nachricht); // Die Nachricht
            messageData.put("timestamp", Timestamp.now()); // Aktueller Zeitstempel

            // Nachricht in die "Chat" Sammlung des Mieters speichern
            db.collection("Mieter")
                    .document(renterDocId)
                    .collection("Chat")
                    .add(messageData)
                    .addOnSuccessListener(documentReference ->
                            Log.d("Firestore", "Nachricht an Mieter " + renterDocId + " gesendet."))
                    .addOnFailureListener(e ->
                            Log.w("Firestore", "Fehler beim Senden der Nachricht an " + renterDocId, e));

        }

        // Erfolgs-Feedback mit Animation
        AnimationUtil.animateInputAndDrawableColor(messageInput, currentDrawableColor, successColor, animationDuration);
        AnimationUtil.animateInputAndDrawableColor(recipientTextView, currentDrawableColor, successColor, animationDuration);

        // Felder nach einer kurzen Verzögerung zurücksetzen
        new android.os.Handler().postDelayed(() -> {
            messageInput.setText("");
            recipientTextView.setText("");
            renterList.clear();
            selectedRenter = new boolean[renterArray.length];
        }, animationDuration);

        // Toast, der den Erfolg anzeigt
        Toast.makeText(this, "Nachricht erfolgreich gesendet", Toast.LENGTH_SHORT).show();
    }

    private void initializeBackToPreviousActivityButton() {
        Button returnButton = findViewById(R.id.sendMessageToMainButton);
        returnButton.setOnClickListener(view -> switchToPreviousActivity());
    }
    private void switchToPreviousActivity() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
    }

}
