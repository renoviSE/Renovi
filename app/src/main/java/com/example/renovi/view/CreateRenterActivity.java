package com.example.renovi.view;

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
import com.example.renovi.viewmodel.Session;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateRenterActivity extends AppCompatActivity {

    EditText renterFirstnameInput, renterLastnameInput, createRenterRent, createRenterSquareMeters;
    TextView createRenterAddress;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Session session;
    private Person user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_renter);

        try {
            getUserFromSession();

            renterFirstnameInput = findViewById(R.id.renterFirstnameInput);
            renterLastnameInput = findViewById(R.id.renterLastnameInput);
            createRenterRent = findViewById(R.id.create_renter_rent);
            createRenterAddress = findViewById(R.id.create_renter_address);
            createRenterSquareMeters = findViewById(R.id.create_renter_squaremeters);

            createRenterAddress.setOnClickListener(v -> showAddressSelectionDialog());

            Button saveButton = findViewById(R.id.create_renter_Button);
            saveButton.setOnClickListener(v -> saveRenterToDatabase());
            initializeBackToPreviousActivityButton();
        } catch (Exception e) {
            Log.e("CreateRenterActivity", "Error initializing activity", e);
            Toast.makeText(this, "Fehler beim Initialisieren der Aktivität", Toast.LENGTH_LONG).show();
            finish(); // Schließt die Aktivität, falls ein kritischer Fehler aufgetreten ist
        }
    }

    private void getUserFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void showAddressSelectionDialog() {
        // Beispiel für eine Methode, um eine Dialogbox zur Auswahl der Adresse anzuzeigen
        // Hier könntest du MultiSelectDialogUtil verwenden, wenn du mehrere Adressen erlauben möchtest
        // oder eine andere Logik, je nach deiner Implementierung.
        Toast.makeText(this, "Adresse auswählen", Toast.LENGTH_SHORT).show();
    }

    private void saveRenterToDatabase() {
        // Daten sammeln
        String vorname = renterFirstnameInput.getText().toString();
        String nachname = renterLastnameInput.getText().toString();
        String mietpreis = createRenterRent.getText().toString();
        String adresse = createRenterAddress.getText().toString();
        String quadratmeter = createRenterSquareMeters.getText().toString();

        // Farben für die Animation
        int dangerColor = ContextCompat.getColor(this, R.color.danger);
        int successColor = ContextCompat.getColor(this, R.color.lightBlue);
        int currentDrawableColor = ContextCompat.getColor(this, R.color.gray2);
        int animationDuration = 1000; // Dauer der Animation in Millisekunden

        // Validierung der Eingabefelder
        boolean isValid = true;
        if (vorname.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(renterFirstnameInput, dangerColor, animationDuration);
            isValid = false;
        }
        if (nachname.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(renterLastnameInput, dangerColor, animationDuration);
            isValid = false;
        }
        if (mietpreis.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(createRenterRent, dangerColor, animationDuration);
            isValid = false;
        }
        if (adresse.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(createRenterAddress, dangerColor, animationDuration);
            isValid = false;
        }
        if (quadratmeter.isEmpty()){
            AnimationUtil.animateHintAndDrawableColor(createRenterSquareMeters, dangerColor, animationDuration);
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Bitte alle erforderlichen Felder ausfüllen", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> renterData = new HashMap<>();
        renterData.put("vorname", vorname);
        renterData.put("nachname", nachname);
        renterData.put("miete", mietpreis);
        renterData.put("adresse", adresse);
        renterData.put("qm", quadratmeter);
        renterData.put("vermieter", user.getId());

        // Log die gesammelten Daten
        Log.d("Firestore", "Renter Data: " + renterData.toString());

        // Erstelle das Mieter-Dokument in Firestore
        db.collection("Mieter").add(renterData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Mieter erfolgreich gespeichert. ID: " + documentReference.getId());

                    // Erfolgsanimation
                    AnimationUtil.animateInputAndDrawableColor(renterFirstnameInput, currentDrawableColor, successColor, animationDuration);
                    AnimationUtil.animateInputAndDrawableColor(renterLastnameInput, currentDrawableColor, successColor, animationDuration);
                    AnimationUtil.animateInputAndDrawableColor(createRenterRent, currentDrawableColor, successColor, animationDuration);
                    AnimationUtil.animateInputAndDrawableColor(createRenterAddress, currentDrawableColor, successColor, animationDuration);
                    AnimationUtil.animateInputAndDrawableColor(createRenterSquareMeters, currentDrawableColor, successColor, animationDuration);

                    // Verzögere das Leeren der Felder bis die Animation abgeschlossen ist
                    new android.os.Handler().postDelayed(() -> {
                        // Felder leeren
                        renterFirstnameInput.setText("");
                        renterLastnameInput.setText("");
                        createRenterRent.setText("");
                        createRenterAddress.setText("");
                        createRenterSquareMeters.setText("");

                        Toast.makeText(CreateRenterActivity.this, "Mieter erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
                    }, animationDuration); // Dauer der Verzögerung entspricht der Animationsdauer

                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Fehler beim Speichern des Mieters", e);
                    Toast.makeText(CreateRenterActivity.this, "Fehler beim Speichern des Mieters", Toast.LENGTH_SHORT).show();
                });
    }

    private void initializeBackToPreviousActivityButton() {
        Button returnButton = findViewById(R.id.CreateRenterToMainButton);
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
