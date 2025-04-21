package com.example.renovi.view;


import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;

import com.example.renovi.R;

public class PrivacyPolicyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        initializeBackToSettingsButton();
    }

    private void initializeBackToSettingsButton() {
        Button startButton = findViewById(R.id.chatToPreviousButton2);
        startButton.setOnClickListener(view -> finish());
    }
}
