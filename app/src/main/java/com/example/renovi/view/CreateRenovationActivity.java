package com.example.renovi.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
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
import java.util.Locale;
import java.util.Map;

public class CreateRenovationActivity extends AppCompatActivity {

    EditText renovationCostInput, createRenovationTimestamp, createRenovationParagraph;
    TextView benefits, states, object, renter;
    boolean[] selectedBenefit;
    boolean[] selectedRenter;
    int[] selectedState = {-1}; // Für Single-Choice, -1 bedeutet keine Auswahl
    int[] selectedObject = {-1}; // Für Single-Choice, -1 bedeutet keine Auswahl
    ArrayList<Integer> benefitsList = new ArrayList<>();
    ArrayList<Integer> renterList = new ArrayList<>();
    ArrayList<String> renterDocIds = new ArrayList<>();
    String[] benefitsArray = {"Brandschutz", "Einbruchschutz", "Isolation"};
    String[] statesArray = {"gut", "mittel", "schlecht"};
    String[] objectsArray = {"Tür", "Fenster", "WC", "Dach"};
    String[] renterArray;  // Dynamisch befüllt

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;
    private Calendar renovationDate = Calendar.getInstance(); // Zum Speichern des Datums

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_renovation);

        try {
            getUserFromSession();

            renovationCostInput = findViewById(R.id.renovationCostInput);
            createRenovationTimestamp = findViewById(R.id.createRenovationTimestamp);
            createRenovationParagraph = findViewById(R.id.createRenovationParagraph);
            benefits = findViewById(R.id.create_renovation_benefits);
            states = findViewById(R.id.create_renovation_state);
            object = findViewById(R.id.create_renovation_object);
            renter = findViewById(R.id.create_renovation_Renter);

            selectedBenefit = new boolean[benefitsArray.length];

            loadRenter();  // Lade die Mieter aus der Firebase-Datenbank

            createRenovationTimestamp.setOnClickListener(v -> showDatePickerDialog());

            benefits.setOnClickListener(v ->
                    MultiSelectDialogUtil.showMultiSelectDialog(this, getString(R.string.selection_view_benefits_title),
                            benefitsArray, selectedBenefit, benefitsList, benefits)
            );

            states.setOnClickListener(v ->
                    MultiSelectDialogUtil.showSingleSelectDialog(this, getString(R.string.selection_view_states_title),
                            statesArray, selectedState, states)
            );

            object.setOnClickListener(v ->
                    MultiSelectDialogUtil.showSingleSelectDialog(this, getString(R.string.selection_view_renovations_title),
                            objectsArray, selectedObject, object)
            );

            renter.setOnClickListener(v -> {
                if (renterArray != null && renterArray.length > 0) {
                    // Update the selectedRenter array based on renterList
                    selectedRenter = new boolean[renterArray.length];
                    for (int i = 0; i < renterArray.length; i++) {
                        if (renterList.contains(i)) {
                            selectedRenter[i] = true;
                        }
                    }

                    // Show the multi-select dialog
                    MultiSelectDialogUtil.showMultiSelectDialog(this, getString(R.string.selection_view_renter_title),
                            renterArray, selectedRenter, renterList, renter);
                }
            });

            Button saveButton = findViewById(R.id.create_renovation_Button);
            saveButton.setOnClickListener(v -> saveRenovationToDatabase());
            initializeBackToPreviousActivityButton();
        } catch (Exception e) {
            Log.e("CreateRenovationActivity", "Error initializing activity", e);
            Toast.makeText(this, "Fehler beim Initialisieren der Aktivität", Toast.LENGTH_LONG).show();
            finish(); // Schließt die Aktivität, falls ein kritischer Fehler aufgetreten ist
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            renovationDate.set(Calendar.YEAR, year);
            renovationDate.set(Calendar.MONTH, monthOfYear);
            renovationDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            createRenovationTimestamp.setText(sdf.format(renovationDate.getTime()));
        }, renovationDate.get(Calendar.YEAR), renovationDate.get(Calendar.MONTH), renovationDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
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
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String firstName = document.getString("vorname");
                            String lastName = document.getString("nachname");
                            String fullName = firstName + " " + lastName;
                            renters.add(fullName);
                            renterDocIds.add(document.getId()); // Dokument-ID speichern
                        }
                        renterArray = renters.toArray(new String[0]);  // Konvertiere in ein Array
                    } else {
                        Log.d("FirebaseError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void saveRenovationToDatabase() {
        // Daten sammeln
        String kosten = renovationCostInput.getText().toString();
        String timestamp = createRenovationTimestamp.getText().toString();
        String paragraph = createRenovationParagraph.getText().toString();
        String state = selectedState[0] != -1 ? statesArray[selectedState[0]] : "";
        String renovationObject = selectedObject[0] != -1 ? objectsArray[selectedObject[0]] : "";

        // Umwandlung der Benefits in einen String, getrennt durch Kommas
        StringBuilder benefitsBuilder = new StringBuilder();
        for (int i = 0; i < benefitsList.size(); i++) {
            benefitsBuilder.append(benefitsArray[benefitsList.get(i)]);
            if (i != benefitsList.size() - 1) {
                benefitsBuilder.append(", ");
            }
        }
        String vorteile = benefitsBuilder.toString();

        // Farben für die Animation
        int dangerColor = ContextCompat.getColor(this, R.color.danger);
        int successColor = ContextCompat.getColor(this, R.color.lightBlue);
        int currentDrawableColor = ContextCompat.getColor(this, R.color.gray2);
        int animationDuration = 1000; // Dauer der Animation in Millisekunden

        // Validierung der Eingabefelder, nicht required sind vorteile, mieter, paragraph
        boolean isValid = true;
        if (kosten.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(renovationCostInput, dangerColor, animationDuration);
            isValid = false;
        }
        if (timestamp.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(createRenovationTimestamp, dangerColor, animationDuration);
            isValid = false;
        }
        if (state.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(states, dangerColor, animationDuration);
            isValid = false;
        }
        if (renovationObject.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(object, dangerColor, animationDuration);
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Bitte alle erforderlichen Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> renovationData = new HashMap<>();
        renovationData.put("kosten", kosten);
        renovationData.put("eintrittsdatum", new Timestamp(renovationDate.getTime()));
        renovationData.put("paragraph", paragraph);
        renovationData.put("vorteile", vorteile);
        renovationData.put("zustand", state);
        renovationData.put("object", renovationObject);
        renovationData.put("nachteile", ""); // Immer leer

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
                    AnimationUtil.animateInputAndDrawableColor(renovationCostInput, currentDrawableColor, successColor, animationDuration);
                    AnimationUtil.animateInputAndDrawableColor(createRenovationTimestamp, currentDrawableColor, successColor, animationDuration);
                    AnimationUtil.animateInputAndDrawableColor(createRenovationParagraph, currentDrawableColor, successColor, animationDuration);
                    AnimationUtil.animateInputAndDrawableColor(benefits, currentDrawableColor, successColor, animationDuration);
                    AnimationUtil.animateInputAndDrawableColor(states, currentDrawableColor, successColor, animationDuration);
                    AnimationUtil.animateInputAndDrawableColor(object, currentDrawableColor, successColor, animationDuration);
                    AnimationUtil.animateInputAndDrawableColor(renter, currentDrawableColor, successColor, animationDuration);

                    // Verzögere das Leeren der Felder bis die Animation abgeschlossen ist
                    new android.os.Handler().postDelayed(() -> {
                        // Felder leeren
                        renovationCostInput.setText("");
                        createRenovationTimestamp.setText("");
                        createRenovationParagraph.setText("");
                        benefits.setText("");
                        states.setText("");
                        object.setText("");
                        renter.setText("");

                        // Leeren der Auswahllisten
                        benefitsList.clear();
                        renterList.clear();
                        selectedState[0] = -1;
                        selectedObject[0] = -1;
                        selectedRenter = new boolean[renterArray.length];
                    }, animationDuration); // Dauer der Verzögerung entspricht der Animationsdauer

                    Toast.makeText(CreateRenovationActivity.this, "Renovierung erfolgreich gespeichert", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Fehler beim Speichern der Renovierung", e);
                    Toast.makeText(CreateRenovationActivity.this, "Fehler beim Speichern der Renovierung", Toast.LENGTH_SHORT).show();
                });
    }

    private void initializeBackToPreviousActivityButton() {
        Button returnButton = findViewById(R.id.CreateRenovationToMainButton);
        returnButton.setOnClickListener(view -> switchToPreviousActivity());
    }
    private void switchToPreviousActivity() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}