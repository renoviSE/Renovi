package com.example.renovi.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.CreateRenovationViewModel;
import com.example.renovi.viewmodel.UI.AnimationUtil;
import com.example.renovi.viewmodel.UI.MultiSelectDialogUtil;
import com.example.renovi.viewmodel.Session;
import com.example.renovi.viewmodel.UI.UIHelper;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateRenovationActivity extends AppCompatActivity {

    EditText renovationCostInput, createRenovationTimestamp, createRenovationParagraph;
    TextView benefits, object, renter;
    boolean[] selectedBenefit;
    boolean[] selectedRenter;
    int[] selectedObject = {-1}; // Für Single-Choice, -1 bedeutet keine Auswahl
    ArrayList<Integer> benefitsList = new ArrayList<>();
    ArrayList<Integer> renterList = new ArrayList<>();
    ArrayList<String> renterDocIds = new ArrayList<>();
    String[] benefitsArray = {"Brandschutz", "Einbruchschutz", "Isolation"};
    String[] objectsArray = {"Tür", "Fenster", "WC", "Dach"};
    String[] renterArray;  // Dynamisch befüllt

    private Session session;
    private Person user;
    private Calendar renovationDate = Calendar.getInstance(); // Zum Speichern des Datums
    private CreateRenovationViewModel viewModel = new CreateRenovationViewModel();

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
            object = findViewById(R.id.create_renovation_object);
            renter = findViewById(R.id.create_renovation_Renter);

            selectedBenefit = new boolean[benefitsArray.length];

            loadRenter();  // Lade die Mieter aus der Firebase-Datenbank

            createRenovationTimestamp.setOnClickListener(v -> showDatePickerDialog());

            benefits.setOnClickListener(v ->
                    MultiSelectDialogUtil.showMultiSelectDialog(this, getString(R.string.selection_view_benefits_title),
                            benefitsArray, selectedBenefit, benefitsList, benefits)
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

            UIHelper.initializeViewFunction(this, R.id.create_renovation_Button, v -> saveRenovationToDatabase());
            UIHelper.initializeBackButton(this, R.id.CreateRenovationToMainButton);
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
        viewModel.getRenters(user, new CreateRenovationViewModel.RenterCallback() {
            @Override
            public void onSuccess(ArrayList<String> ids, ArrayList<String> renters) {
                renterDocIds = ids;
                renterArray = renters.toArray(new String[0]);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void saveRenovationToDatabase() {
        // Daten sammeln
        String kosten = renovationCostInput.getText().toString();
        String timestamp = createRenovationTimestamp.getText().toString();
        String paragraph = createRenovationParagraph.getText().toString();
        String state = "gut";
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
        Log.d("Firestore", "Renovation Data: " + renovationData);

        viewModel.saveRenovationToDatabase(renovationData, renterDocIds, new CreateRenovationViewModel.RenovationCallback() {
            @Override
            public void onSuccess() {
                // Erfolgsanimation
                AnimationUtil.animateInputAndDrawableColor(renovationCostInput, currentDrawableColor, successColor, animationDuration);
                AnimationUtil.animateInputAndDrawableColor(createRenovationTimestamp, currentDrawableColor, successColor, animationDuration);
                AnimationUtil.animateInputAndDrawableColor(createRenovationParagraph, currentDrawableColor, successColor, animationDuration);
                AnimationUtil.animateInputAndDrawableColor(benefits, currentDrawableColor, successColor, animationDuration);
                AnimationUtil.animateInputAndDrawableColor(object, currentDrawableColor, successColor, animationDuration);
                AnimationUtil.animateInputAndDrawableColor(renter, currentDrawableColor, successColor, animationDuration);

                // Verzögere das Leeren der Felder bis die Animation abgeschlossen ist
                new android.os.Handler().postDelayed(() -> {
                    // Felder leeren
                    renovationCostInput.setText("");
                    createRenovationTimestamp.setText("");
                    createRenovationParagraph.setText("");
                    benefits.setText("");
                    object.setText("");
                    renter.setText("");

                    // Leeren der Auswahllisten
                    benefitsList.clear();
                    renterList.clear();
                    selectedObject[0] = -1;
                    selectedRenter = new boolean[renterArray.length];
                }, animationDuration); // Dauer der Verzögerung entspricht der Animationsdauer

                Toast.makeText(CreateRenovationActivity.this, "Renovierung erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(CreateRenovationActivity.this, "Fehler beim Speichern der Renovierung", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}