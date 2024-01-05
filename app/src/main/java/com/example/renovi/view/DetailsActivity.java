package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initializeBackToMainButton();
        initializeReadMoreLink();
        
    }
    private void initializeBackToMainButton() {
        Button startButton = findViewById(R.id.InboxToMainButton);
        startButton.setOnClickListener(view -> switchToMain());
    }

    private void switchToMain() {
        Intent switchActivityIntent = new Intent(this, MainActivityTest.class);
        startActivity(switchActivityIntent);
    }

    private void initializeReadMoreLink() { //reade more redirects to website
        TextView descriptionString = findViewById(R.id.descriptionString);
        descriptionString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage("https://www.gesetze-im-internet.de/bgb/__555b.html");
            }
        });
    }
    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Keine Anwendung gefunden, um diese URL zu öffnen", Toast.LENGTH_SHORT).show();
        }
    }
}