package com.example.renovi.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.renovi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    final String TAG = "myTag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        getRenovierungen(db);
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

    private void getRenovierungen(FirebaseFirestore db) {
        Log.i(TAG, "We´re trying");
        db.collection("Renovierung")
                // document = eingeloggter User! 
                .whereEqualTo("mieter", db.collection("mieter").document("W6vJ0B7y1ahLHynv02AD"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documents) {
                        // Daten erfolgreich erhalten
                        for (QueryDocumentSnapshot document : documents) {
                            // Hier kannst du die Daten verarbeiten, z.B., um Buttons zu erstellen
                            String mieterName = "NEUER BUTTON";
                            // Erstelle einen Button für jeden Mieter
                            //erstelleButtonFürMieter(mieterName);
                            Log.i(TAG, "Good Job");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Fehler beim Abrufen der Daten
                        Log.i(TAG, "NO");

                    }
                });
    }

    private void initializeOverviewButton() {
        Button startButton = findViewById(R.id.mailButton);
        startButton.setOnClickListener(view -> switchToDetails(1));
    }

    private void switchToDetails(int renoID) {
        Intent switchActivityIntent = new Intent(this, DetailsActivity.class);
        switchActivityIntent.putExtra("renoID",renoID);
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

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void erstelleButtonFürMieter(String mieterName) {
        RelativeLayout meinLayout = findViewById(R.id.layout_activity_main);

        if (mieterName != null) {
            Button renoButton = new Button(this);
            renoButton.setText(mieterName);
            renoButton.setId(R.id.mailButton); // Setze die ID des Buttons
            renoButton.setWidth((340)); // Konvertiere dp in Pixel
            renoButton.setHeight(dpToPx(85)); // Konvertiere dp in Pixel

            renoButton.setPadding(0, 0, 0, 0); // Entferne Padding
            renoButton.setTextSize(14);
            renoButton.setTypeface(null, Typeface.BOLD);
            renoButton.setTextColor(ContextCompat.getColor(this, R.color.gray4));
            renoButton.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_lightblue));

            // Definiere Layoutparameter für den Button
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.addRule(RelativeLayout.ALIGN_END, R.id.reparaturenBackground);
            layoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.reparaturenBackground);
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.reparaturenBackground);
            layoutParams.setMarginEnd(dpToPx(24)); // Konvertiere dp in Pixel

            // Setze die Layoutparameter für den Button
            renoButton.setLayoutParams(layoutParams);

            // Füge den neuen Button zum Layout hinzu
            meinLayout.addView(renoButton);
        }


}

}