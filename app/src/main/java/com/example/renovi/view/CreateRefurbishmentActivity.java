package com.example.renovi.view;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class CreateRefurbishmentActivity extends AppCompatActivity {
    EditText refurbishmentCostInput, createRefurbishmentTimestamp;
    TextView object; // Bezieht sich auf die Adresse

    int[] selectedObjectIndex = {-1}; // Für Single-Choice, -1 bedeutet keine Auswahl
    ArrayList<String> renterDocIds = new ArrayList<>();
    ArrayList<String> objectList = new ArrayList<>(); // Liste der Adressen

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;
    private Calendar refurbishmentDate = Calendar.getInstance(); // Zum Speichern des Datums

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_refurbishment);

        try {
            getUserFromSession();

            refurbishmentCostInput = findViewById(R.id.refurbishmentCostInput);
            createRefurbishmentTimestamp = findViewById(R.id.createRefurbishmentTimestamp);
            object = findViewById(R.id.create_refurbishment_object);

            loadObjects();  // Lade die Adressen aus der Firebase-Datenbank

            // Setze OnClickListener für den DatePicker
            createRefurbishmentTimestamp.setOnClickListener(v -> showDatePickerDialog());

            object.setOnClickListener(v -> {
                if (!objectList.isEmpty()) {
                    // Show the single-select dialog für die Adressen
                    MultiSelectDialogUtil.showSingleSelectDialog(this, getString(R.string.selection_view_refurbishment_title),
                            objectList.toArray(new String[0]), selectedObjectIndex, object);
                }
            });

            Button saveButton = findViewById(R.id.create_refurbishment_Button);
            saveButton.setOnClickListener(v -> saveRenovationToDatabase());
            initializeBackToPreviousActivityButton();
        } catch (Exception e) {
            Log.e("CreateRefurbishmentActivity", "Error initializing activity", e);
            Toast.makeText(this, "Fehler beim Initialisieren der Aktivität", Toast.LENGTH_LONG).show();
            finish(); // Schließt die Aktivität, falls ein kritischer Fehler aufgetreten ist
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            refurbishmentDate.set(Calendar.YEAR, year);
            refurbishmentDate.set(Calendar.MONTH, monthOfYear);
            refurbishmentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            createRefurbishmentTimestamp.setText(sdf.format(refurbishmentDate.getTime()));
        }, refurbishmentDate.get(Calendar.YEAR), refurbishmentDate.get(Calendar.MONTH), refurbishmentDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void loadObjects() {
        db.collection("Mieter")
                .whereEqualTo("vermieter", user.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashSet<String> uniqueAddresses = new HashSet<>(); // HashSet zur Vermeidung von Duplikaten
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String address = document.getString("adresse");
                            if (address != null && uniqueAddresses.add(address)) { // Wenn die Adresse nicht bereits hinzugefügt wurde
                                objectList.add(address); // Adresse zur Liste hinzufügen
                                renterDocIds.add(document.getId()); // Dokument-ID speichern
                            }
                        }
                    } else {
                        Log.d("FirebaseError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void saveRenovationToDatabase() {
        // Daten sammeln
        String kosten = refurbishmentCostInput.getText().toString();
        String timestamp = createRefurbishmentTimestamp.getText().toString();
        String selectedObjectText = selectedObjectIndex[0] != -1 ? objectList.get(selectedObjectIndex[0]) : "";

        // Farben für die Animation
        int dangerColor = ContextCompat.getColor(this, R.color.danger);
        int successColor = ContextCompat.getColor(this, R.color.lightBlue);
        int currentDrawableColor = ContextCompat.getColor(this, R.color.gray2);
        int animationDuration = 1000; // Dauer der Animation in Millisekunden

        // Validierung der Eingabefelder
        boolean isValid = true;
        if (kosten.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(refurbishmentCostInput, dangerColor, animationDuration);
            isValid = false;
        }
        if (timestamp.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(createRefurbishmentTimestamp, dangerColor, animationDuration);
            isValid = false;
        }
        if (selectedObjectText.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(object, dangerColor, animationDuration);
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Bitte alle erforderlichen Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        // Erstelle das Renovierung-Dokument in Firestore
        Map<String, Object> renovationData = new HashMap<>();
        renovationData.put("kosten", kosten);
        renovationData.put("datum", new Timestamp(refurbishmentDate.getTime()));
        renovationData.put("adresse", selectedObjectText);

        Log.d("Firestore", "Renovation Data: " + renovationData.toString());

        db.collection("Renovierung").add(renovationData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Renovierung erfolgreich gespeichert. ID: " + documentReference.getId());

                    // Verknüpfe das Renovierung-Dokument mit den Mietern
                    if (selectedObjectIndex[0] != -1) {
                        String renterDocId = renterDocIds.get(selectedObjectIndex[0]);
                        DocumentReference renterDocRef = db.collection("Mieter").document(renterDocId);
                        renterDocRef.collection("Renovierungen").add(renovationData)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Renovierung für Mieter erfolgreich verknüpft."))
                                .addOnFailureListener(e -> Log.w("Firestore", "Fehler bei der Verknüpfung der Renovierung mit dem Mieter", e));
                    }

                    // Erfolgsanimation
                    AnimationUtil.animateInputAndDrawableColor(refurbishmentCostInput, currentDrawableColor, successColor, animationDuration);
                    AnimationUtil.animateInputAndDrawableColor(object, currentDrawableColor, successColor, animationDuration);

                    // Verzögere das Leeren der Felder bis die Animation abgeschlossen ist
                    new android.os.Handler().postDelayed(() -> {
                        refurbishmentCostInput.setText("");
                        object.setText("");

                    }, animationDuration); // Dauer der Verzögerung entspricht der Animationsdauer

                    Toast.makeText(CreateRefurbishmentActivity.this, "Renovierung erfolgreich gespeichert", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Fehler beim Speichern der Renovierung", e);
                    Toast.makeText(CreateRefurbishmentActivity.this, "Fehler beim Speichern der Renovierung", Toast.LENGTH_SHORT).show();
                });
    }

    private void initializeBackToPreviousActivityButton() {
        Button returnButton = findViewById(R.id.CreateRefurbishmentToMainButton);
        returnButton.setOnClickListener(view -> switchToPreviousActivity());
    }

    private void switchToPreviousActivity() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
    }
}
