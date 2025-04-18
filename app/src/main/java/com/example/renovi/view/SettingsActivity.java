package com.example.renovi.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.renovi.R;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup themeRadioGroup;
    private RadioButton lightModeRadioButton, darkModeRadioButton, systemModeRadioButton;
    private Button backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Apply saved theme if user changed it before
        applySavedTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        lightModeRadioButton = findViewById(R.id.lightModeRadioButton);
        darkModeRadioButton = findViewById(R.id.darkModeRadioButton);
        systemModeRadioButton = findViewById(R.id.systemModeRadioButton);
        backButton = findViewById(R.id.settingsBackButton);

        // Mark the current selection
        int savedMode = getSavedThemeMode();
        switch (savedMode) {
            case AppCompatDelegate.MODE_NIGHT_NO:
                lightModeRadioButton.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                darkModeRadioButton.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            default:
                systemModeRadioButton.setChecked(true);
                break;
        }

        // Change listener
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedMode;

            if (checkedId == R.id.lightModeRadioButton) {
                selectedMode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.darkModeRadioButton) {
                selectedMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                selectedMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }

            // Save and apply only if user selects something
            saveThemeMode(selectedMode);
            AppCompatDelegate.setDefaultNightMode(selectedMode);
        });

        // Back
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void saveThemeMode(int mode) {
        SharedPreferences prefs = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("NightMode", mode);
        editor.apply();
    }

    private int getSavedThemeMode() {
        SharedPreferences prefs = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE);
        // Default to FOLLOW_SYSTEM if not set
        return prefs.getInt("NightMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE);
        if (prefs.contains("NightMode")) {
            int savedMode = prefs.getInt("NightMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            AppCompatDelegate.setDefaultNightMode(savedMode);
        } else {
            // No preference yet, follow system
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
