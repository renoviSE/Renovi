package com.example.renovi.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Spinner languageSpinner;
    private Button backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        languageSpinner = findViewById(R.id.languageSpinner);
        backButton = findViewById(R.id.settingsBackButton);

        // Sprachauswahl setzen
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        String currentLang = LocaleHelper.getLanguage(this);
        if (currentLang.startsWith("de")) {
            languageSpinner.setSelection(0);
        } else {
            languageSpinner.setSelection(1); // "en" oder was auch immer im Array steht
        }

        // Listener für Sprachauswahl
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstSelection = true; // verhindert Direktreaktion beim Öffnen

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }

                String selectedLanguage = position == 1 ? "en" : "de";

                if (!LocaleHelper.getLanguage(SettingsActivity.this).equals(selectedLanguage)) {
                    LocaleHelper.setLocale(SettingsActivity.this, selectedLanguage);

                    // sanfter Neustart – keine Flags, kein Layout-Salat
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // SettingsActivity schließen
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Zurück-Button
        backButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


}
