package com.example.renovi.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;
import com.example.renovi.viewmodel.SettingsViewModel;
import com.example.renovi.viewmodel.UI.UIHelper;

public class SettingsActivity extends AppCompatActivity {

    private Spinner languageSpinner;
    private SettingsViewModel settingsViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsViewModel = new SettingsViewModel();

        UIHelper.initializeBackButton(this, R.id.settingsBackButton);

        languageSpinner = findViewById(R.id.languageSpinner);

        initializeDropMenu();

        languageSpinner.setSelection(settingsViewModel.getLanguageIndex(this));

        initializeLanguageSelection();
    }

    private void initializeLanguageSelection() {
        // Listener für Sprachauswahl
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstSelection = true; // verhindert Direktreaktion beim Öffnen

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }

                if (settingsViewModel.changeLanguage(SettingsActivity.this, position)) {
                    // sanfter Neustart – keine Flags, kein Layout-Salat
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // SettingsActivity schließen
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initializeDropMenu() {
        // Sprachauswahl setzen
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


}
