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
    TextView address;

    ArrayList<Integer> selectedAddressesIndices = new ArrayList<>();
    ArrayList<String> renterDocIds = new ArrayList<>();
    ArrayList<String> addressList = new ArrayList<>();
    ArrayList<Double> qmList = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;
    private Calendar refurbishmentDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_refurbishment);

        try {
            getUserFromSession();

            refurbishmentCostInput = findViewById(R.id.refurbishmentCostInput);
            createRefurbishmentTimestamp = findViewById(R.id.createRefurbishmentTimestamp);
            address = findViewById(R.id.create_refurbishment_address);

            loadObjects();

            // Date Picker Dialog
            createRefurbishmentTimestamp.setOnClickListener(v -> showDatePickerDialog());

            // Multi-Select Dialog for addresses
            address.setOnClickListener(v -> {
                if (!addressList.isEmpty()) {
                    MultiSelectDialogUtil.showMultiSelectDialog(
                            this,
                            getString(R.string.selection_view_refurbishment_title),
                            addressList.toArray(new String[0]),
                            new boolean[addressList.size()],
                            selectedAddressesIndices,
                            address
                    );
                }
            });

            Button saveButton = findViewById(R.id.create_refurbishment_Button);
            saveButton.setOnClickListener(v -> saveRenovationToDatabase());
            initializeBackToPreviousActivityButton();
        } catch (Exception e) {
            Log.e("CreateRefurbishmentActivity", "Error initializing activity", e);
            Toast.makeText(this, "Fehler beim Initialisieren der Aktivität", Toast.LENGTH_LONG).show();
            finish();
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
                        HashSet<String> uniqueAddresses = new HashSet<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String address = document.getString("adresse");
                            String qmString = document.getString("qm");
                            if (address != null && uniqueAddresses.add(address)) {
                                addressList.add(address);
                                renterDocIds.add(document.getId());
                                try {
                                    qmList.add(Double.parseDouble(qmString));
                                } catch (NumberFormatException e) {
                                    Log.e("LoadObjects", "Invalid qm value: " + qmString, e);
                                    qmList.add(0.0);
                                }
                            }
                        }
                    } else {
                        Log.d("FirebaseError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void saveRenovationToDatabase() {
        String kostenInput = refurbishmentCostInput.getText().toString();
        String timestamp = createRefurbishmentTimestamp.getText().toString();

        // Farben für Validierung
        int dangerColor = ContextCompat.getColor(this, R.color.danger);
        int animationDuration = 1000;

        boolean isValid = true;
        if (kostenInput.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(refurbishmentCostInput, dangerColor, animationDuration);
            isValid = false;
        }
        if (timestamp.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(createRefurbishmentTimestamp, dangerColor, animationDuration);
            isValid = false;
        }
        if (selectedAddressesIndices.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(address, dangerColor, animationDuration);
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Bitte alle erforderlichen Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalCost;
        try {
            totalCost = Double.parseDouble(kostenInput);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ungültiger Betrag für Kosten", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int addressIndex : selectedAddressesIndices) {
            String address = addressList.get(addressIndex);

            // Hole Mieter für die Adresse
            db.collection("Mieter")
                    .whereEqualTo("adresse", address)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        double totalSquareMeters = 0.0;
                        ArrayList<QueryDocumentSnapshot> renters = new ArrayList<>();

                        for (QueryDocumentSnapshot renter : queryDocumentSnapshots) {
                            renters.add(renter);
                            String qmString = renter.getString("qm");
                            try {
                                totalSquareMeters += Double.parseDouble(qmString);
                            } catch (NumberFormatException e) {
                                Log.w("Renovierung", "Fehler beim Parsen von Quadratmetern für Mieter: " + renter.getId(), e);
                            }
                        }

                        // Verteilung der Kosten
                        for (QueryDocumentSnapshot renter : renters) {
                            String renterId = renter.getId();
                            String qmString = renter.getString("qm");
                            double renterSquareMeters = 0.0;

                            try {
                                renterSquareMeters = Double.parseDouble(qmString);
                            } catch (NumberFormatException e) {
                                Log.w("Renovierung", "Fehler beim Parsen von Quadratmetern für Mieter: " + renterId, e);
                            }

                            // Kostenberechnung und Rundung auf 2 Nachkommastellen
                            double renterCost = totalSquareMeters > 0 ? (renterSquareMeters / totalSquareMeters) * totalCost : 0.0;
                            renterCost = Math.round(renterCost * 100.0) / 100.0;

                            // Renovierungsdaten erstellen
                            Map<String, Object> renovationData = new HashMap<>();
                            renovationData.put("kosten", String.valueOf(renterCost));
                            renovationData.put("eintrittsdatum", new Timestamp(refurbishmentDate.getTime()));
                            renovationData.put("object", "Fenster");
                            renovationData.put("zustand", "gut");
                            renovationData.put("vorteile", "");
                            renovationData.put("nachteile", "");
                            renovationData.put("paragraph", "");

                            // Renovierung für den Mieter direkt in seiner Sammlung "Renovierungen" speichern
                            DocumentReference renterDocRef = db.collection("Mieter").document(renterId);
                            renterDocRef.collection("Renovierungen")
                                    .add(renovationData)
                                    .addOnSuccessListener(aVoid -> Log.d("Renovierung", "Renovierung erfolgreich hinzugefügt für Mieter: " + renterId))
                                    .addOnFailureListener(e -> Log.w("Renovierung", "Fehler beim Hinzufügen der Renovierung", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.w("Renovierung", "Fehler beim Abrufen der Mieter für Adresse: " + address, e));
        }

        Toast.makeText(this, "Renovierungen erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
    }



    private void initializeBackToPreviousActivityButton() {
        Button returnButton = findViewById(R.id.CreateRefurbishmentToMainButton);
        returnButton.setOnClickListener(view -> switchToPreviousActivity());
    }

    private void switchToPreviousActivity() {
        finish();
    }
}
