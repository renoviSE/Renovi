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
import com.example.renovi.viewmodel.CreateRefurbishmentViewModel;
import com.example.renovi.viewmodel.UI.AnimationUtil;
import com.example.renovi.viewmodel.UI.UIHelper;
import com.example.renovi.viewmodel.UI.MultiSelectDialogUtil;
import com.example.renovi.viewmodel.Session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CreateRefurbishmentActivity extends AppCompatActivity {
    EditText refurbishmentCostInput, apportionableCostInput, createRefurbishmentTimestamp;
    TextView address;

    ArrayList<Integer> selectedAddressesIndices = new ArrayList<>();
    ArrayList<String> renterDocIds = new ArrayList<>();
    ArrayList<String> addressList = new ArrayList<>();
    ArrayList<Double> qmList = new ArrayList<>();

    private Session session;
    private Person user;
    private Calendar refurbishmentDate = Calendar.getInstance();
    private CreateRefurbishmentViewModel viewModel = new CreateRefurbishmentViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_refurbishment);

        try {
            getUserFromSession();

            refurbishmentCostInput = findViewById(R.id.refurbCostInput);
            createRefurbishmentTimestamp = findViewById(R.id.dateInput);
            address = findViewById(R.id.create_refurbishment_address);
            apportionableCostInput = findViewById(R.id.refurbApportionableCostInput);

            loadRenterAddress();

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

            UIHelper.initializeViewFunction(this, R.id.create_refurbishment_Button, v -> saveRenovationToDatabase());
            UIHelper.initializeBackButton(this, R.id.refurbishmentBackButton);
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

    private void loadRenterAddress() {
        viewModel.getRenterAdresses(user, new CreateRefurbishmentViewModel.RenterCallback() {
            @Override
            public void onSuccess(ArrayList<String> addresses, ArrayList<String> renterIds, ArrayList<Double> qms) {
                renterDocIds = renterIds;
                addressList = addresses;
                qmList = qms;
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void saveRenovationToDatabase() {
        String kostenInput = refurbishmentCostInput.getText().toString();
        String apportionableCost = apportionableCostInput.getText().toString();
        String timestamp = createRefurbishmentTimestamp.getText().toString();

        // Farben für Validierung
        int dangerColor = ContextCompat.getColor(this, R.color.danger);
        int animationDuration = 1000;

        boolean isValid = true;
        if (kostenInput.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(refurbishmentCostInput, dangerColor, animationDuration);
            isValid = false;
        }
        if (apportionableCost.isEmpty()) {
            AnimationUtil.animateHintAndDrawableColor(apportionableCostInput, dangerColor, animationDuration);
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

        viewModel.saveRenovationToDatabase(apportionableCost, refurbishmentDate, selectedAddressesIndices, addressList, new CreateRefurbishmentViewModel.RenovationCallback() {
            @Override
            public void onSuccess() {
                // Farben für die Animation
                int successColor = ContextCompat.getColor(CreateRefurbishmentActivity.this, R.color.lightBlue);
                int currentDrawableColor = ContextCompat.getColor(CreateRefurbishmentActivity.this, R.color.gray2);
                int animationDuration = 1000; // Dauer der Animation in Millisekunden
                // Erfolgsanimation
                AnimationUtil.animateInputAndDrawableColor(refurbishmentCostInput, currentDrawableColor, successColor, animationDuration);
                AnimationUtil.animateInputAndDrawableColor(createRefurbishmentTimestamp, currentDrawableColor, successColor, animationDuration);
                AnimationUtil.animateInputAndDrawableColor(address, currentDrawableColor, successColor, animationDuration);
                AnimationUtil.animateInputAndDrawableColor(apportionableCostInput, currentDrawableColor, successColor, animationDuration);


                // Verzögere das Leeren der Felder bis die Animation abgeschlossen ist
                new android.os.Handler().postDelayed(() -> {
                    // Felder leeren
                    refurbishmentCostInput.setText("");
                    createRefurbishmentTimestamp.setText("");
                    apportionableCostInput.setText("");

                    // Leeren der Auswahllisten
                    addressList.clear();
                }, animationDuration); // Dauer der Verzögerung entspricht der Animationsdauer

                Toast.makeText(CreateRefurbishmentActivity.this, "Renovierungen erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(CreateRefurbishmentActivity.this, "Ungültiger Betrag für Kosten", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}
