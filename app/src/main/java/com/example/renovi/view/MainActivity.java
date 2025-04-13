package com.example.renovi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.renovi.R;
import com.example.renovi.model.Landlord;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String TAG = "myTag";
    public static final String geplanteRenovierung ="com.exemple.renovi";
    private Person user;
    private Session session;
    private ProgressBar rentCostProgressBar;
    private List<Pair<String, Class<?>>> menuItems;
    private ConstraintLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getUserFromSession();

        // Wait for the layout to be completely drawn
        mainLayout = findViewById(R.id.inner_constraint);

        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to prevent repeated calls
                mainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                if (user instanceof Renter) {
                    getRenovierungen(db);
                } else if (user instanceof Landlord) {
                    generateMenuButtons();
                }

                setUserNameAsHeadline();
                initializeButtons();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // soll nicht wieder ins Login
    }

    private void getUserFromSession() {
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
        // Zugriff auf die Sammlung "Renovationen" des aktuellen Mieters
        db.collection("Mieter").document(user.getId()).collection("Renovierungen")
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

                            buttonId += 1;
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

    private void initializeMenuItems() {
        menuItems = new ArrayList<>();
        menuItems.add(new Pair<>("Mieter", RenterListActivity.class));
        menuItems.add(new Pair<>("Mieter hinzufügen", CreateRenterActivity.class));
        menuItems.add(new Pair<>("Renovierung hinzufügen", CreateRenovationActivity.class));
        menuItems.add(new Pair<>("Maßnahme hinzufügen", CreateRefurbishmentActivity.class));
    }

    public void generateMenuButtons() {
        initializeMenuItems();

        ButtonCreator buttonCreator = new ButtonCreator(this);
        TextView buttonsTitle = buttonCreator.createUpcomingSectionTitle(mainLayout, R.string.menu_title_string, R.id.header_constraint);

        for (Pair<String, Class<?>> menuItem : menuItems) {
            String objectName = menuItem.first;
            Class<?> destination = menuItem.second;

            Button menuButton = buttonCreator.createButton(mainLayout, objectName, R.id.mainScrollSpacer);

            Intent intent = new Intent(this, destination);
            menuButton.setOnClickListener(view -> startActivity(intent));
        }
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
        Button mainButton = findViewById(R.id.addMessageButton);
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
        // Erstellen und Hinzufügen des Buttons zum Layout
        if (renovationTitle != null) {
            ButtonCreator buttonCreator  = new ButtonCreator(this);
            TextView buttonsTitle = buttonCreator.createUpcomingSectionTitle(mainLayout, R.string.bevorstehende_renovierungen_title, R.id.header_constraint);

            Button renoButton = buttonCreator.createButton(mainLayout, renovationTitle, R.id.mainScrollSpacer);
            renoButton.setOnClickListener(view -> switchToDetails(renovation)); //hier renoId eigentlich
        }
    }

    private void generatePlaceholder() {
        // Erstellen und Hinzufügen des Platzhalers zum Layout
        ButtonCreator buttonCreator  = new ButtonCreator(this);
        TextView buttonsTitle = buttonCreator.createUpcomingSectionTitle(mainLayout, R.string.bevorstehende_renovierungen_title, R.id.header_constraint);
        int topConstraint = buttonsTitle.getId();

        buttonCreator.createPlaceholderView(mainLayout, topConstraint, R.string.no_renovations_placeholder_message);

    }

    private void generateRentBannerForRenter() {
        // Erstellen und Hinzufügen des Banners zum Layout
        BannerCreator bannerCreator  = new BannerCreator(this);
        bannerCreator.createBanner(mainLayout, R.id.mainScrollSpacer);
    }
    public static int dpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

}