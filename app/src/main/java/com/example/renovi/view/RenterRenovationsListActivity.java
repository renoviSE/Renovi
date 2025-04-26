package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.model.Renovation;
import com.example.renovi.viewmodel.RenterRenovationsListViewModel;
import com.example.renovi.viewmodel.UI.ButtonCreator;
import com.example.renovi.viewmodel.UI.UIHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RenterRenovationsListActivity extends AppCompatActivity {

    private ConstraintLayout mainLayout;
    private RenterRenovationsListViewModel renterRenovationsListViewModel = new RenterRenovationsListViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_renter_renovations);
        mainLayout = findViewById(R.id.renovationsListConstraintLayout);

        UIHelper.initializeBackButton(this, R.id.renterRenovationsToPreviousButton);

        // Empfangen des Intent, der diese Activity gestartet hat
        Intent intent = getIntent();

        // Extrahieren der übertragenen Variable 'renterId'
        if (intent != null && intent.hasExtra("renterId")) {
            String renterId = intent.getStringExtra("renterId");

            getRenovierungen(renterId);

        } else {
            Toast.makeText(this, "Keine Mieter-ID übertragen!", Toast.LENGTH_LONG).show();
        }
    }

    private void getRenovierungen(String renterId) {
        renterRenovationsListViewModel.getRenovations(renterId, new RenterRenovationsListViewModel.RenterRenovationsListCallback() {
            @Override
            public void onRenovationList(LinkedHashMap<String, Renovation> renovationList) {
                for (Map.Entry<String, Renovation> entry : renovationList.entrySet()) {
                    generateButtonForRenter(entry.getKey(), entry.getValue());
                }
            }

            @Override
            public void onEmptyList() {
                generatePlaceholder();
            }
        });
    }

    private void generateButtonForRenter(String renovationTitle, Renovation renovation) {
        // Erstellen und Hinzufügen des Buttons zum Layout
        if (renovationTitle != null) {
            ButtonCreator buttonCreator = new ButtonCreator(this);

            Button renoButton = buttonCreator.createButton(mainLayout, renovationTitle, R.id.renovationsListScrollSpacer);
            renoButton.setOnClickListener(view -> switchToDetails(renovation));
        }
    }

    private void switchToDetails(Renovation renovation) {
        Intent switchActivityIntent = new Intent(this, DetailsActivity.class);
        switchActivityIntent.putExtra("renovierung", renovation);
        startActivity(switchActivityIntent);
    }

    private void generatePlaceholder() {
        // Erstellen und Hinzufügen des Platzhalters zum Layout
        ButtonCreator buttonCreator = new ButtonCreator(this);

        buttonCreator.createPlaceholderView(mainLayout, R.id.renovationsListTopConstraintForPlaceholder, R.string.no_renovations_placeholder_message);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}