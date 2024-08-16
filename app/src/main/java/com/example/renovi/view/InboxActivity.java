package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.renovi.R;

public class InboxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        initializeBackToPreviousActivityButton();
    }
    private void initializeBackToPreviousActivityButton() {
        Button returnButton = findViewById(R.id.InboxToMainButton);
        returnButton.setOnClickListener(view -> switchToPreviousActivity());
    }
    private void switchToPreviousActivity() {
        finish(); // Beendet die aktuelle Activity und kehrt zur vorherigen im Stack zurück
    }
}