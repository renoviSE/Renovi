package com.example.renovi.view;


import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;

import com.example.renovi.R;
import com.example.renovi.model.LocaleHelper;

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
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

}
