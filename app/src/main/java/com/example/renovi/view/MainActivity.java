package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.model.Person;
import com.example.renovi.viewmodel.BannerCreator;
import com.example.renovi.viewmodel.ButtonCreator;
import com.example.renovi.model.Renovation;
import com.example.renovi.model.Renter;
import com.example.renovi.viewmodel.Session;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.net.Uri;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    final String TAG = "myTag";
    public static final String geplanteRenovierung ="com.exemple.renovi";
    private Person user;
    private Session session;
    private ProgressBar rentCostProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getRenterFromSession();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (user instanceof Renter) {
            getRenovierungen(db);
        }

        setContentView(R.layout.activity_main);

        setUserNameAsHeadline();
        initializeButtons();
    }

    @Override
    public void onBackPressed() {
        // soll nicht wieder ins Login
    }

    private void getRenterFromSession() {
        session = Session.getInstance(this);
        user = session.getUser();
    }

    private void initializeButtons() {
        initializeInboxButton();
        initializeProfileButton();
        initializeMainButton();
        initializeFaqButton();
    }


    private void getRenovierungen(FirebaseFirestore db) {
        db.collection("Renovierung")
                .whereEqualTo("mieter", db.collection("mieter").document(user.getId()))
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
                        generatePlaceholder();
                    }

                    ((Renter) user).updateRent(allObjectsValue);
                    ((Renter) user).setRentDifferenceInPercentage(allObjectsValue);
                    setRentCost();
                })
                .addOnFailureListener(e -> {
                    // Fehler beim Abrufen der Daten
                    Log.i(TAG, "NO");

                });
    }


    private void setUserNameAsHeadline() {
        TextView userName = findViewById(R.id.userName);
        userName.setText(user.getFirstName() + " " + user.getLastName());
    }

    private void setRentCost() {
        generateRentBannerForRenter();


        TextView rentcostString = findViewById(R.id.mietpreisString);
        rentcostString.setText(((Renter) user).getRoundedRentasString() + "€");

        TextView bannerDescription = findViewById(R.id.bannerDescription);
        String formattedPercentage = String.format("%.0f%%", ((Renter) user).getRentDifferenceInPercentage());
        String bannerDescriptionText = getString(R.string.banner_description_string, formattedPercentage);
        bannerDescription.setText(bannerDescriptionText);


        ProgressBar rentCostProgressBar = findViewById(R.id.rentCostProgressBar);
        rentCostProgressBar.setVisibility(View.VISIBLE);
        rentCostProgressBar.setProgress(((Renter) user).getRentDifferenceInPercentage().intValue());
        rentCostProgressBar.setMax(100);
    }

    private void switchToDetails(Renovation renovation) {
        Intent switchActivityIntent = new Intent(this, DetailsActivity.class);
        switchActivityIntent.putExtra("renovierung", renovation);
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

    private void generateButtonForRenter(String renovationTitle, Renovation renovation) {
        // Zugriff auf das Haupt-ConstraintLayout
        ConstraintLayout mainLayout = findViewById(R.id.inner_constraint);

        // Erstellen und Hinzufügen des Buttons zum Layout
        if (renovationTitle != null) {
            ButtonCreator buttonCreator  = new ButtonCreator(this);
            Button renoButton = buttonCreator.createButton(mainLayout, renovationTitle);
            renoButton.setOnClickListener(view -> switchToDetails(renovation)); //hier renoId eigentlich
        }
    }

    private void generatePlaceholder() {
        // Zugriff auf das Haupt-ConstraintLayout
        ConstraintLayout mainLayout = findViewById(R.id.inner_constraint);

        // Erstellen und Hinzufügen des Platzhalers zum Layout
        ButtonCreator buttonCreator  = new ButtonCreator(this);
        buttonCreator.createNoRenovationsView(mainLayout);

    }

    private void generateRentBannerForRenter() {
        // Zugriff auf das Haupt-ConstraintLayout
        ConstraintLayout mainLayout = findViewById(R.id.inner_constraint);

        // Erstellen und Hinzufügen des Banners zum Layout
        BannerCreator bannerCreator  = new BannerCreator(this);
        bannerCreator.createBanner(mainLayout);
    }
    public static int dpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

}