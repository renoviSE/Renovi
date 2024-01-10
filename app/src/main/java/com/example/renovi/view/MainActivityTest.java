package com.example.renovi.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import android.net.Uri;
public class MainActivityTest extends AppCompatActivity {

    private ProgressBar rentProgressBar;
    private TextView rentCostPercentage;
    private ProgressBar co2ProgressBar;
    private ProgressBar doorPriceProgressBar;
    private ProgressBar doorEfficiencyProgressBar;
    private Button lastButton;
    final String TAG = "myTag";
    public static final String geplanteRenovierung ="com.exemple.renovi";

    ScrollView mainScrollView;
    TextView upcomingRenovationsTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        getRenovierungen(db);

        initializeButtons();
        declareViews();
        initializeViews();
    }

    private void initializeButtons() {
        initializeOverviewButton();
        initializeInboxButton();
        initializeProfileButton();
        initializeMainButton();
        initializeFaqButton();
    }

    private void declareViews() {
        rentProgressBar = (ProgressBar) findViewById(R.id.rentCostProgressBar);
        rentCostPercentage = (TextView) findViewById(R.id.rentCostPercentage);
        co2ProgressBar = (ProgressBar) findViewById(R.id.co2ProgressBar);
        doorPriceProgressBar = (ProgressBar) findViewById(R.id.doorPriceProgressBar);
        doorEfficiencyProgressBar = (ProgressBar) findViewById(R.id.doorEfficiencyProgressBar);
    }

    private void initializeViews() {
        int rentCurrentProgress = 20;
        String rentCurrentCostPercentage = String.format("%d%%", rentCurrentProgress);
        rentCostPercentage.setText(rentCurrentCostPercentage);
        rentProgressBar.setProgress(rentCurrentProgress);
        rentProgressBar.setMax(100);


        int co2CurrentProgress = 75;
        co2ProgressBar.setProgress(co2CurrentProgress);
        co2ProgressBar.setMax(100);


        int doorCurrentPrice = 36;
        doorPriceProgressBar.setProgress(doorCurrentPrice);
        doorPriceProgressBar.setMax(100);

        int doorCurrentEfficiency = 55;
        doorEfficiencyProgressBar.setProgress(doorCurrentEfficiency);
        doorEfficiencyProgressBar.setMax(100);

    }

    private void getRenovierungen(FirebaseFirestore db) {
        Log.i(TAG, "We´re trying");
        db.collection("Renovierung")
                // document = eingeloggter User!
                .whereEqualTo("mieter", db.collection("mieter").document("W6vJ0B7y1ahLHynv02AD"))
                .get()
                .addOnSuccessListener(documents -> {
                    // Daten erfolgreich erhalten
                    int buttonId = 1;
                    for (DocumentSnapshot document : documents.getDocuments()) {
                        if (document.exists()) {
                            String objectValue = document.getString("object");
                            // Erstelle einen Button für jeden Mieter
                            erstelleButtonFuerMieter(objectValue,buttonId);
                            buttonId+=1;
                            Log.i(TAG, "Good Job");
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    // Fehler beim Abrufen der Daten
                    Log.i(TAG, "NO");

                });
    }

    private void initializeOverviewButton() {
        Button startButton = findViewById(R.id.mailButton);
        startButton.setOnClickListener(view -> switchToDetails(1));
    }

    private void switchToDetails(int renoID) {
        Intent switchActivityIntent = new Intent(this, DetailsActivity.class);
        switchActivityIntent.putExtra(geplanteRenovierung,renoID);
        startActivity(switchActivityIntent);
    }

    //Redudanz kann man bestimmt lösen @TODO

    private void initializeInboxButton() {
        Button notificationButton = findViewById(R.id.notificationButton);
        notificationButton.setOnClickListener(view -> switchToInbox());
        Button notificationButtonNavBar = findViewById(R.id.notificationButtonNavBar);
        notificationButtonNavBar.setOnClickListener(view -> switchToInbox());
    }
    private void switchToInbox() {
        Intent switchActivityIntent = new Intent(this, InboxActivity.class);
        startActivity(switchActivityIntent);
    }

    private void initializeProfileButton() {
        Button profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(view -> switchToProfile());
    }
    private void switchToProfile() {
        Intent switchActivityIntent = new Intent(this, ProfileActivity.class);
        switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(switchActivityIntent);
        overridePendingTransition(0,0); //disables animation
    }

    private void initializeMainButton() { //main button scrolls down to the TextView "upcomingRenovationsTitle"
        Button mainButton = findViewById(R.id.navBarButton);
        mainScrollView = findViewById(R.id.scrollView2);
        upcomingRenovationsTitle = findViewById(R.id.upcomingRenovationsTitle);
        mainButton.setOnClickListener(view -> scrollToTextView());
    }
    private void scrollToTextView() {
        mainScrollView.post(() -> {
            mainScrollView.smoothScrollTo(0, upcomingRenovationsTitle.getTop()); // smoothScrollTo(horizontalScroll, verticalScroll)
        });
    }

    private void initializeFaqButton() {
        Button faqButton = findViewById(R.id.faqButton);
        faqButton.setOnClickListener(view -> openWebPage("https://funktionales-kostensplitting.de"));
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


    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void erstelleButtonFuerMieter(String mieterName, int buttonId) {
        ConstraintLayout constraintLayout = findViewById(R.id.inner_constraint);

        if (mieterName != null) {
            Button renoButton = new Button(this);
            renoButton.setText(mieterName);
            renoButton.setId(buttonId);
            renoButton.setWidth(340);
            renoButton.setHeight(dpToPx(85));
            renoButton.setPadding(0, 0, 0, 0);
            renoButton.setTextSize(14);
            renoButton.setTypeface(null, Typeface.BOLD);
            renoButton.setTextColor(ContextCompat.getColor(this, R.color.gray4));
            renoButton.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_lightblue));

            constraintLayout.addView(renoButton);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            if (buttonId == 1) {
                constraintSet.connect(renoButton.getId(), ConstraintSet.TOP, R.id.upcomingRenovationsTitle, ConstraintSet.BOTTOM);
            }else{
                constraintSet.connect(renoButton.getId(), ConstraintSet.TOP, lastButton.getId(), ConstraintSet.BOTTOM);
            }
            constraintSet.connect(renoButton.getId(), ConstraintSet.END, R.id.verbrauchssenkungTitle, ConstraintSet.END);
            constraintSet.connect(R.id.verbrauchssenkungTitle,ConstraintSet.TOP, renoButton.getId(),ConstraintSet.BOTTOM);

            constraintSet.applyTo(constraintLayout);

            lastButton = renoButton;

        }

    }
}