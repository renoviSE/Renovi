package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.viewmodel.ButtonCreator;
import com.example.renovi.model.Renovation;
import com.example.renovi.model.Renter;
import com.example.renovi.viewmodel.RenterSession;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.net.Uri;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    final String TAG = "myTag";
    public static final String geplanteRenovierung ="com.exemple.renovi";
    private Renter renter;
    private RenterSession renterSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getRenterFromSession();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        getRenovierungen(db);

        setContentView(R.layout.activity_main);

        setRenterNameAsHeadline();
        initializeButtons();
    }

    @Override
    public void onBackPressed() {
        // soll nicht wieder ins Login
    }

    private void getRenterFromSession() {
        renterSession = new RenterSession(this);
        renter = renterSession.getRenter();
    }

    private void initializeButtons() {
        initializeInboxButton();
        initializeProfileButton();
        initializeMainButton();
        initializeFaqButton();
    }


    private void getRenovierungen(FirebaseFirestore db) {
        Log.i(TAG, "We´re trying");
        db.collection("Renovierung")
                // document = eingeloggter User!
                .whereEqualTo("mieter", db.collection("mieter").document(renter.getId()))
                .get()
                .addOnSuccessListener(documents -> {
                    BigDecimal allObjectsValue = new BigDecimal("0");

                    // Daten erfolgreich erhalten
                    int buttonId = 1;
                    for (DocumentSnapshot document : documents.getDocuments()) {
                        if (document.exists()) {
                            String objectName = document.getString("object");
                            Renovation renovation = new Renovation(document.getString("object"), document.getString("vorteile"), document.getString("nachteile"), document.getString("kosten"), document.getString("paragraph"), document.getString("zustand"));
                            // Erstelle einen Button für jeden Mieter
                            generateButtonForRenter(objectName, renovation);

                            // Speichere Kosten von allen Objekten
                            allObjectsValue = allObjectsValue.add(renovation.getObjectValue());

                            buttonId+=1;
                            Log.i(TAG, "Good Job");
                        }
                    }
                    // Wenn keine Renovierungen vorhanden sind
                    if (buttonId == 1) {
                        TextView noRenovations = findViewById(R.id.noRenovations);
                        noRenovations.setVisibility(View.VISIBLE);
                    }

                    renter.setRent(allObjectsValue);
                    renter.setRentDifferenceInPercentage(allObjectsValue);
                    setRentCost();
                })
                .addOnFailureListener(e -> {
                    // Fehler beim Abrufen der Daten
                    Log.i(TAG, "NO");

                });
    }


    private void setRenterNameAsHeadline() {
        TextView userName = findViewById(R.id.userName);
        userName.setText(renter.getFirstName() + " " + renter.getLastName());
    }

    private void setRentCost() {
        TextView rentcostString = findViewById(R.id.mietpreisString);
        rentcostString.setText(renter.getRoundedRentasString() + "€");

        TextView bannerDescription = findViewById(R.id.bannerDescription);
        String formattedPercentage = String.format("%.0f%%", renter.getRentDifferenceInPercentage());
        String bannerDescriptionText = getString(R.string.banner_description_string, formattedPercentage);
        bannerDescription.setText(bannerDescriptionText);


        ProgressBar rentCostProgressBar = findViewById(R.id.rentCostProgressBar);
        rentCostProgressBar.setProgress(renter.getRentDifferenceInPercentage().intValue());
        rentCostProgressBar.setMax(100);
    }

    private void initializeOverviewButton() {
        Button startButton = findViewById(R.id.mailButton);
        //startButton.setOnClickListener(view -> switchToDetails("1"));
    }

    private void switchToDetails(Renovation renovierung) {
        Intent switchActivityIntent = new Intent(this, DetailsActivity.class);
        switchActivityIntent.putExtra("renovierung", renovierung);
        startActivity(switchActivityIntent);
    }

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
        overridePendingTransition(0,0); //disables animation
        startActivity(switchActivityIntent);
    }

    private void initializeMainButton() { //main button scrolls down
        Button mainButton = findViewById(R.id.navBarButton);
        mainButton.setOnClickListener(view -> scrollToTextView());
    }
    private void scrollToTextView() {
        ScrollView mainScrollView = findViewById(R.id.scrollView2);

        mainScrollView.post(() -> {
            mainScrollView.smoothScrollTo(0, mainScrollView.getBottom()); // smoothScrollTo(horizontalScroll, verticalScroll)
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

    private void generateButtonForRenter(String renovationTitle, Renovation renovierung) {
        // Zugriff auf das Haupt-ConstraintLayout
        ConstraintLayout mainLayout = findViewById(R.id.inner_constraint);

        // Erstellen und Hinzufügen des Buttons zum Layout
        if (renovationTitle != null) {
            ButtonCreator buttonCreator  = new ButtonCreator(this);
            Button renoButton = buttonCreator.createButton(mainLayout, renovationTitle);
            renoButton.setOnClickListener(view -> switchToDetails(renovierung)); //hier renoId eigentlich
        }
    }
}