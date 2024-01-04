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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.net.Uri;
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

    ScrollView mainScrollView;
    TextView upcomingRenovationsTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        getRenovierungen(db);
        initializeOverviewButton();
        initializeInboxButton();
        initializeProfileButton();
        initializeMainButton();
        initializeFaqButton();

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
                            erstelleButtonFürMieter(mieterName);
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
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToTextView();
            }
        });
    }
    private void scrollToTextView() {
        mainScrollView.post(new Runnable() {
            @Override
            public void run() {
                mainScrollView.smoothScrollTo(0, upcomingRenovationsTitle.getTop()); // smoothScrollTo(horizontalScroll, verticalScroll)
            }
        });
    }

    private void initializeFaqButton() {
        Button faqButton = findViewById(R.id.faqButton);
        faqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage("https://funktionales-kostensplitting.de");
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


    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void erstelleButtonFürMieter(String mieterName) {
        ConstraintLayout constraintLayout = findViewById(R.id.inner_constraint);

        if (mieterName != null) {
            Button renoButton = new Button(this);
            renoButton.setText(mieterName);
            renoButton.setId(View.generateViewId());
            renoButton.setWidth(340);
            renoButton.setHeight(dpToPx(85));
            renoButton.setPadding(0, 0, 0, 0);
            renoButton.setTextSize(14);
            renoButton.setTypeface(null, Typeface.BOLD);
            renoButton.setTextColor(ContextCompat.getColor(this, R.color.gray4));
            renoButton.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_lightblue));

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(renoButton.getId(), ConstraintSet.TOP, R.id.upcomingRenovationsTitle, ConstraintSet.BOTTOM);
            constraintSet.connect(renoButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.connect(renoButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID ,constraintSet.START);

            // Füge Constraints für die ScrollView hinzu (optional, wenn nötig)
            //constraintSet.connect(R.id.upcomingRenovationsTitle, ConstraintSet.TOP, R.id.scrollView2, ConstraintSet.TOP);
            //constraintSet.connect(R.id.reparaturenBackground, ConstraintSet.BOTTOM, R.id.scrollView2, ConstraintSet.BOTTOM);

            constraintSet.applyTo(constraintLayout);

            //@TODO ICH HABE MEINEN BUTTON VERLOREN :(
        }
    }
}