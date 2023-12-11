package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.renovi.R;

public class MainActivityTest extends AppCompatActivity {

    private int rentCurrentProgress;
    private ProgressBar rentProgressBar;
    private String rentCurrentCostPercentage;
    private TextView rentCostPercentage;
    private int co2CurrentProgress;
    private ProgressBar co2ProgressBar;
    private int doorCurrentPrice;
    private ProgressBar doorPriceProgressBar;
    private int doorCurrentEfficiency;
    private ProgressBar doorEfficiencyProgressBar;
    private int windowCurrentPrice;
    private ProgressBar windowPriceProgressBar;
    private int windowCurrentEfficiency;
    private ProgressBar windowEfficiencyProgressBar;
    private int wcRenovationCurrentPrice;
    private ProgressBar wcRenovationPriceProgressBar;
    private int wcRenovationCurrentEfficiency;
    private ProgressBar wcRenovationEfficiencyProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        initializeOverviewButton();
        initializeInboxButton();

        rentProgressBar = (ProgressBar) findViewById(R.id.rentCostProgressBar);
        rentCostPercentage = (TextView) findViewById(R.id.rentCostPercentage);
        co2ProgressBar = (ProgressBar) findViewById(R.id.co2ProgressBar);
        doorPriceProgressBar = (ProgressBar) findViewById(R.id.doorPriceProgressBar);
        doorEfficiencyProgressBar = (ProgressBar) findViewById(R.id.doorEfficiencyProgressBar);
        windowPriceProgressBar = (ProgressBar) findViewById(R.id.windowPriceProgressBar);
        windowEfficiencyProgressBar = (ProgressBar) findViewById(R.id.windowEfficiencyProgressBar);
        wcRenovationPriceProgressBar = (ProgressBar) findViewById(R.id.wcRenovationPriceProgressBar);
        wcRenovationEfficiencyProgressBar = (ProgressBar) findViewById(R.id.wcRenovationEfficiencyProgressBar);


        rentCurrentProgress = 20;
        rentCurrentCostPercentage = String.format("%d%%", rentCurrentProgress);
        rentCostPercentage.setText(rentCurrentCostPercentage);
        rentProgressBar.setProgress(rentCurrentProgress);
        rentProgressBar.setMax(100);


        co2CurrentProgress = 75;
        co2ProgressBar.setProgress(co2CurrentProgress);
        co2ProgressBar.setMax(100);


        doorCurrentPrice = 36;
        doorPriceProgressBar.setProgress(doorCurrentPrice);
        doorPriceProgressBar.setMax(100);

        doorCurrentEfficiency = 55;
        doorEfficiencyProgressBar.setProgress(doorCurrentEfficiency);
        doorEfficiencyProgressBar.setMax(100);

        doorCurrentPrice = 27;
        windowPriceProgressBar.setProgress(doorCurrentPrice);
        windowPriceProgressBar.setMax(100);

        doorCurrentEfficiency = 75;
        windowEfficiencyProgressBar.setProgress(doorCurrentEfficiency);
        windowEfficiencyProgressBar.setMax(100);

        wcRenovationCurrentPrice = 60;
        wcRenovationPriceProgressBar.setProgress(wcRenovationCurrentPrice);
        wcRenovationPriceProgressBar.setMax(100);

        wcRenovationCurrentEfficiency = 85;
        wcRenovationEfficiencyProgressBar.setProgress(wcRenovationCurrentEfficiency);
        wcRenovationEfficiencyProgressBar.setMax(100);
    }
    private void initializeOverviewButton() {
        Button startButton = findViewById(R.id.mailButton);
        startButton.setOnClickListener(view -> switchToDetails());
    }

    private void switchToDetails() {
        Intent switchActivityIntent = new Intent(this, DetailsActivity.class);
        startActivity(switchActivityIntent);
    }

    //Redudanz kann man bestimmt lösen @TODO

    private void initializeInboxButton() {
        Button startButton = findViewById(R.id.notificationButton);
        startButton.setOnClickListener(view -> switchToInbox());
    }

    private void switchToInbox() {
        Intent switchActivityIntent = new Intent(this, InboxActivity.class);
        startActivity(switchActivityIntent);
    }
}