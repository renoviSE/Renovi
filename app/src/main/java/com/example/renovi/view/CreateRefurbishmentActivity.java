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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class CreateRefurbishmentActivity extends AppCompatActivity {
    EditText refurbishmentCostInput;
    TextView  object;

    boolean[] selectedObject;
    int[] selectedState = {-1}; // Für Single-Choice, -1 bedeutet keine Auswahl
    ArrayList<Integer> renterList = new ArrayList<>();
    ArrayList<String> renterDocIds = new ArrayList<>();
    String[] statesArray = {"gut", "mittel", "schlecht"};
    String[] objectsArray = {"Tür", "Fenster", "WC", "Dach"};
    HashSet<String> objects = new HashSet<String>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_refurbishment);

        try {
            getUserFromSession();

            refurbishmentCostInput = findViewById(R.id.renovationCostInput);
            object = findViewById(R.id.create_refurbishment_object);

            loadObjects();  // Lade die Adressen aus der Firebase-Datenbank

            object.setOnClickListener(v -> {
                if (objects != null && objects.size() > 0) {

                    selectedObject = new boolean[objects.size()];
                    for (int i = 0; i < objects.size(); i++) {
                        if (renterList.contains(i)) {
                            selectedObject[i] = true;
                        }
                    }


                    String [] objectArray = (String[]) objects.toArray();
                    // Show the multi-select dialog
                    MultiSelectDialogUtil.showMultiSelectDialog(this, getString(R.string.selection_view_refurbishment_title),
                            objectArray, selectedObject, renterList, object);
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
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String address = document.getString("adresse");
                            objects.add(address);
                            renterDocIds.add(document.getId()); // Dokument-ID speichern
                        }
                    } else {
                        Log.d("FirebaseError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void saveRenovationToDatabase() {
        // Daten sammeln
        String kosten = refurbishmentCostInput.getText().toString();

        // Farben für die Animation
        int dangerColor = ContextCompat.getColor(this, R.color.danger);
        int successColor = ContextCompat.getColor(this, R.color.lightBlue);
        int currentDrawableColor = ContextCompat.getColor(this, R.color.gray2);
        int animationDuration = 1000; // Dauer der Animation in Millisekunden

        // Validierung der Eingabefelder, nicht required sind vorteile, mieter, paragraph
        boolean isValid = true;
        if (kosten.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(refurbishmentCostInput, dangerColor, animationDuration);
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Bitte alle erforderlichen Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> renovationData = new HashMap<>();
        renovationData.put("kosten", kosten);

        // Log die gesammelten Daten
        Log.d("Firestore", "Renovation Data: " + renovationData.toString());

        // Erstelle das Renovierung-Dokument in Firestore
        db.collection("Renovierung").add(renovationData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Renovierung erfolgreich gespeichert. ID: " + documentReference.getId());

                    // Verknüpfe das Renovierung-Dokument mit den Mietern
                    for (int index : renterList) {
                        String renterDocId = renterDocIds.get(index);
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
                        // Felder leeren
                        refurbishmentCostInput.setText("");
                        object.setText("");

                        // Leeren der Auswahllisten
                        renterList.clear();
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
